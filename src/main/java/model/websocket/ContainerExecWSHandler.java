package model.websocket;

import model.Docker;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class ContainerExecWSHandler extends TextWebSocketHandler {
    private Map<String,ExecSession> execSessionMap=new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Properties properties = new Properties();
        properties.load(Docker.class.getClassLoader().getResourceAsStream("settings.properties"));
        String dockerIP = properties.getProperty("dockerIP");
        int dockerPort = Integer.parseInt(properties.getProperty("dockerPort"));
        String command = session.getAttributes().get("command").toString();
        String containerId = session.getAttributes().get("containerId").toString();
        String user = session.getAttributes().get("execUser").toString();
        //exec
        String execId = Docker.createExec(containerId, command, user);
        //socket
        Socket socket = connectExec(dockerIP, dockerPort, execId);
        //exec message
        getExecMessage(session, dockerIP, containerId, socket);
    }


    private Socket connectExec(String ip,int port, String execId) throws IOException {
        Socket socket=new Socket(ip,port);
        socket.setKeepAlive(true);
        OutputStream out = socket.getOutputStream();
        StringBuffer pw = new StringBuffer();
        pw.append("POST /exec/"+execId+"/start HTTP/1.1\r\n");
        pw.append("Host: " + ip + ":" + port + "\r\n");
        pw.append("User-Agent: Docker-Client\r\n");
        pw.append("Content-Type: application/json\r\n");
        pw.append("Connection: Upgrade\r\n");
        JSONObject obj = new JSONObject();
        obj.put("Detach",false);
        obj.put("Tty",true);
        String json=obj.toString();
        pw.append("Content-Length: "+json.length()+"\r\n");
        pw.append("Upgrade: tcp\r\n");
        pw.append("\r\n");
        pw.append(json);
        out.write(pw.toString().getBytes("UTF-8"));
        out.flush();
        return socket;
    }

    private void getExecMessage(WebSocketSession session, String ip, String containerId, Socket socket) throws IOException {
        InputStream inputStream=socket.getInputStream();
        byte[] bytes=new byte[1024];
        StringBuilder returnMsg = new StringBuilder();
        while(true){
            int n = inputStream.read(bytes);
            String msg=new String(bytes,0,n);
            returnMsg.append(msg);
            bytes=new byte[10240];
            if(returnMsg.indexOf("\r\n\r\n")!=-1){
                session.sendMessage(new TextMessage(returnMsg.substring(returnMsg.indexOf("\r\n\r\n")+4,returnMsg.length())));
                break;
            }
        }
        OutPutThread outPutThread=new OutPutThread(inputStream,session);
        outPutThread.start();
        execSessionMap.put(containerId,new ExecSession(ip,containerId,socket,outPutThread));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        String containerId=session.getAttributes().get("containerId").toString();
        ExecSession execSession=execSessionMap.get(containerId);
        if(execSession!=null){
            execSession.getOutPutThread().interrupt();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String containerId=session.getAttributes().get("containerId").toString();
        ExecSession execSession=execSessionMap.get(containerId);
        OutputStream out = execSession.getSocket().getOutputStream();
        out.write(message.asBytes());
        out.flush();
    }
}
