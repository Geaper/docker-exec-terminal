package model;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class Docker {

    private static DockerClient dockerClient;
    private static String dockerIP;
    private static String dockerPort;

    static {

        try {
            Properties properties = new Properties();
            properties.load(Docker.class.getClassLoader().getResourceAsStream("settings.properties"));
            dockerIP = properties.getProperty("dockerIP");
            dockerPort = properties.getProperty("dockerPort");
            DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost("tcp://" + dockerIP + ":" + dockerPort)
                    .build();

            dockerClient = DockerClientBuilder.getInstance(config).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String execConsole(String containerName, String command, String user) {

        Container container = getContainer(containerName);
        //attach
        //return "ws://" + dockerIP + ":" + dockerPort + "/containers/" + container.getId() + "/attach/ws?logs=1&stdin=1&stderr=1&stdout=1&stream=1";

        //exec
        return "ws://" + dockerIP + ":8080/ws/container/exec?command=" + command + "&execUser=" + user + "&ip=" + dockerIP + "&containerId=" + container.getId();
    }

    public static Container getContainer(String containerName) {
        List<Container> containers  = getContainers();
        for(Container container : containers) {
            String[] containerNames = container.getNames();
            for(String name : containerNames) {
                if(name.equals("/" + containerName)) {
                    return container;
                }
            }
        }
        return null;
    }

    public static List<Container> getContainers() {
        return dockerClient.listContainersCmd().withShowAll(true).exec();
    }

    public static String createExec(String containerId, String command, String user) {
        return dockerClient.execCreateCmd(containerId).withCmd(command).withAttachStdin(true).withAttachStdout(true).withAttachStderr(true).withTty(true).withUser(user).exec().getId();
    }

}
