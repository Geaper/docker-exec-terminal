<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Container Console</title>
    <link rel="stylesheet" href="../../resources/css/xterm/xterm.css">
</head>
<body>
<div class="container-scroller">
    <div class="container-fluid">
        <div class="row row-offcanvas row-offcanvas-right">
            <div class="content-wrapper">
                <h3 class="text-primary mb-4 main-title">Terminal <c:if test="${param.name != null}">for ${param.name} </c:if><a id="refresh-page" data-toggle="tooltip" data-placement="bottom" title="refresh"><span class="fa fa-refresh text-blue"></span></a></h3>
                <div class="row mb-2">
                    <div class="col-lg-12">
                        <div id="connectDiv">
                            <h5 class="top20"><span class="fa fa-list"></span> Connection parameters</h5>
                            <hr>
                            <input type="hidden" id="containerNameUrl" value="${param.name}">
                            <div class="row">
                                <label class="col-sm-2 control-label">User</label>
                                <div class="col-sm-10">
                                    <div class="input-group">
                                        <span class="input-group-addon"><span class="fa fa-user"></span></span>
                                        <input class="form-control" id="user" value="root" />
                                    </div>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <label class="col-sm-2 control-label">Command</label>
                                <div class="col-sm-10">
                                    <div class="input-group">
                                        <span class="input-group-addon"><span class="fa fa-linux"></span></span>
                                        <select class="form-control" id="command">
                                            <option>/bin/bash</option>
                                            <option>/bin/sh</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <hr>
                            <c:if test="${param.name == null}">
                                <div class="row">
                                    <label class="col-sm-2 control-label">Select a container</label>
                                    <div class="col-sm-10">
                                        <div class="input-group">
                                            <span class="input-group-addon"><span class="fa fa-archive"></span></span>
                                            <select class="form-control" id="containerName">
                                                <c:forEach var="container" items="${containers}">
                                                    <c:forEach var="name" items="${container.names}">
                                                        <c:if test="${!name.contains('profile')}">
                                                            <c:set var="containerName" value="${name.replace('/','')}"/>
                                                        </c:if>
                                                    </c:forEach>
                                                    <option>${containerName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <hr>
                            </c:if>
                            <div class="row">
                                <label class="col-sm-2 control-label">Connect to the container</label>
                                <div class="col-sm-10">
                                    <button type="button" class="btn btn-primary" id="btnConnect">Connect</button>
                                </div>
                            </div>
                            <hr>
                        </div>
                        <div id="terminalDiv" style="display: none;">
                            <h5><span class="fa fa-terminal"></span> Terminal</h5>
                            <hr>
                            <div class="row">
                                <label class="col-sm-2 control-label">Disconnect from the container</label>
                                <div class="col-sm-10">
                                    <button type="button" class="btn btn-primary" id="btnDisconnect">Disconnect</button>
                                </div>
                            </div>
                            <hr>
                            <div class="row top20">
                                <div id="terminal"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script src="../resources/js/lib/xterm/xterm.js"></script>
<script src="../resources/js/lib/xterm/attach.js"></script>
<script src="../../resources/js/app.js"></script>
</html>

