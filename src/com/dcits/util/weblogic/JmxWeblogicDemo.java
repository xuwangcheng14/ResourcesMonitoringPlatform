package com.dcits.util.weblogic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import weblogic.health.HealthState;

public class JmxWeblogicDemo {

private static MBeanServerConnection connection;
private static JMXConnector connector;

private static ObjectName runtimeService;

private static final String RUNTIMESERVICEMBEAN = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";

/**
 * 初始化连接
 * 
 * @throws IOException
 * @throws MalformedURLException
 * @throws MalformedObjectNameException
 */
public static void initConnection() throws IOException,
        MalformedURLException, MalformedObjectNameException {
    String protocol = "t3";
    // String hostname = "192.168.161.101";
    // String portString = "7001";
    // String username = "weblogic";
    // String password = "wang1234";
    String hostname = "127.0.0.1";
    String portString = "7001";
    String username = "weblogic";
    String password = "12345678";
    Integer portInteger = Integer.valueOf(portString);
    int port = portInteger.intValue();
    String jndiroot = "/jndi/";
    String mserver = "weblogic.management.mbeanservers.runtime";
    JMXServiceURL serviceURL = new JMXServiceURL(protocol, hostname, port,
            jndiroot + mserver);

    Hashtable<String, String> h = new Hashtable<String, String>();
    h.put(Context.SECURITY_PRINCIPAL, username);
    h.put(Context.SECURITY_CREDENTIALS, password);
    h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
            "weblogic.management.remote");

    connector = JMXConnectorFactory.connect(serviceURL, h);
    connection = connector.getMBeanServerConnection();

    runtimeService = new ObjectName(RUNTIMESERVICEMBEAN);
}

/**
 * 连接池管理
 * 
 * @param serverRuntime
 * @throws AttributeNotFoundException
 * @throws InstanceNotFoundException
 * @throws MBeanException
 * @throws ReflectionException
 * @throws IOException
 */
public void getConnectorServiceRuntime(ObjectName serverRuntime)
        throws AttributeNotFoundException, InstanceNotFoundException,
        MBeanException, ReflectionException, IOException {
    // 应用服务运行时 ApplicationRuntimes
    ObjectName[] applicationRuntimes = getAttribute(serverRuntime,
            "ApplicationRuntimes");
    for (int i = 0; i < applicationRuntimes.length; i++) {
        ObjectName applicationRuntime = applicationRuntimes[i];
        ObjectName[] componentRuntimes = getAttribute(applicationRuntime,
                "ComponentRuntimes");
        for (int j = 0; j < componentRuntimes.length; j++) {
            ObjectName componentRuntime = componentRuntimes[j];
            String type = getAttribute(componentRuntime, "Type");
            System.out.println(type);
            if (!type.equals("ConnectorComponentRuntime")) {
                continue;
            }

            ObjectName[] connectionPools = getAttribute(componentRuntime,
                    "ConnectionPools");
            for (int k = 0; k < connectionPools.length; k++) {
                ObjectName connectionPool = connectionPools[k];
                // 连接池的状态 State
                String state = getAttribute(connectionPool, "State");
                // 服务名称 Name
                String name = getAttribute(connectionPool, "Name");
                // 连接池名称 PoolName
                String poolName = getAttribute(connectionPool, "PoolName");
                // 连接池中的当前使用的连接数量 ActiveConnectionsCurrentCount
                Integer activeConnectionsCurrentCount = getAttribute(
                        connectionPool, "ActiveConnectionsCurrentCount");
                // 等待池中连接的最大客户数 HighestNumWaiters
                Long highestNumWaiters = getAttribute(connectionPool,
                        "HighestNumWaiters");
                // 丢失的连接数 ConnectionLeakProfileCount
                Integer connectionLeakProfileCount = getAttribute(
                        connectionPool, "ConnectionLeakProfileCount");
                // 连接池最大连接数 MaxCapacity
                Integer maxCapacity = getAttribute(connectionPool,
                        "StMaxCapacityate");

                System.out.println(name);
                System.out.println(poolName);
                System.out.println(state);
                System.out.println(activeConnectionsCurrentCount);
                System.out.println(highestNumWaiters);
                System.out.println(connectionLeakProfileCount);
                System.out.println(maxCapacity);

            }
        }

    }
}

/**
 * 执行队列运行时
 * 
 * @param serverRuntime
 * @throws AttributeNotFoundException
 * @throws InstanceNotFoundException
 * @throws MBeanException
 * @throws ReflectionException
 * @throws IOException
 */
public void getExecuteQueueRuntimes(ObjectName serverRuntime)
        throws AttributeNotFoundException, InstanceNotFoundException,
        MBeanException, ReflectionException, IOException {
    // 执行队列运行时 ExecuteQueueRuntimes
    ObjectName[] executeQueueRuntimes = getAttribute(serverRuntime,
            "ExecuteQueueRuntimes");
    for (int i = 0; i < executeQueueRuntimes.length; i++) {
        ObjectName executeQueueRuntime = executeQueueRuntimes[i];
        
        System.out.println("=============================================");
        
        System.out.println(executeQueueRuntime.getKeyPropertyList());
        
        System.out.println("=============================================");
        // 队列的名称 Name
        String name = getAttribute(executeQueueRuntime, "Name");
        // 执行线程的总数目 ExecuteThreadTotalCount
        Integer executeThreadTotalCount = getAttribute(executeQueueRuntime,
                "ExecuteThreadTotalCount");
        // 队列中当前空闲线程数 ExecuteThreadCurrentIdleCount
        Integer executeThreadCurrentIdleCount = getAttribute(
                executeQueueRuntime, "ExecuteThreadCurrentIdleCount");
        // 队列中最长的等待时间 PendingRequestOldestTime
        // 取出相应毫秒值
        Long pendingTime = getAttribute(executeQueueRuntime,
                "PendingRequestOldestTime");
        // 获取毫秒值对应的日期，并转换成指定格式
        Date pendingDate = new Date(pendingTime);
        String pendingRequestOldestTime = formatDate(pendingDate,
                "yyyy/MM/dd HH:mm:ss");
        // 队列中等待的请求数 PendingRequestCurrentCount
        Integer pendingRequestCurrentCount = getAttribute(
                executeQueueRuntime, "PendingRequestCurrentCount");
        // 被本队列处理的请求总数 ServicedRequestTotalCount
        Integer servicedRequestTotalCount = getAttribute(
                executeQueueRuntime, "ServicedRequestTotalCount");

        // TODO
        System.out.println(name);
        System.out.println(executeThreadTotalCount);
        System.out.println(executeThreadCurrentIdleCount);
        System.out.println(pendingRequestOldestTime);
        System.out.println(pendingRequestCurrentCount);
        System.out.println(servicedRequestTotalCount);
    }
}

/**
 * JDBC数据源运行时
 * 
 * @param serverRuntime
 * @throws AttributeNotFoundException
 * @throws InstanceNotFoundException
 * @throws MBeanException
 * @throws ReflectionException
 * @throws IOException
 */
public void getJDBCDataSourceRuntime(ObjectName serverRuntime)
        throws AttributeNotFoundException, InstanceNotFoundException,
        MBeanException, ReflectionException, IOException {
    // JDBC运行时 JDBCServiceRuntime
    ObjectName jdbcServiceRuntime = getAttribute(serverRuntime,
            "JDBCServiceRuntime");
    // TODO
    // System.out.println(getAttribute(
    // jdbcServiceRuntime, "Name"));
    // JDBC数据源运行时 JDBCDataSourceRuntimeMBeans
    ObjectName[] jdbcDataSourceRuntimeMBeans = getAttribute(
            jdbcServiceRuntime, "JDBCDataSourceRuntimeMBeans");

    for (int i = 0; i < jdbcDataSourceRuntimeMBeans.length; i++) {
        ObjectName jdbcDataSourceRuntimeMBean = jdbcDataSourceRuntimeMBeans[i];

        // 名称 Name
        String name = getAttribute(jdbcDataSourceRuntimeMBean, "Name");

        // // 返回当前等待连接的总数 WaitingForConnectionCurrentCount
        Integer waitingForConnectionCurrentCount = getAttribute(
                jdbcDataSourceRuntimeMBean,
                "WaitingForConnectionCurrentCount");
        // 重新连接失败计数 FailuresToReconnectCount
        Integer failuresToReconnectCount = getAttribute(
                jdbcDataSourceRuntimeMBean, "FailuresToReconnectCount");
        // 状态 State (Running,Suspended,Shutdown,Unhealthy,Unknown)
        String state = getAttribute(jdbcDataSourceRuntimeMBean, "State");
        // 泄漏的连接计数 LeakedConnectionCount
        Integer leakedConnectionCount = getAttribute(
                jdbcDataSourceRuntimeMBean, "LeakedConnectionCount");
        // 部署状态 DeploymentState
        Integer deploymentState = getAttribute(jdbcDataSourceRuntimeMBean,
                "DeploymentState");
        
        Integer maxCount = getAttribute(jdbcDataSourceRuntimeMBean, "HighestNumAvailable");
        
         System.out.println(name);
         System.out.println(waitingForConnectionCurrentCount);
         System.out.println(failuresToReconnectCount);
         System.out.println(state);
         System.out.println(leakedConnectionCount);
         System.out.println(deploymentState);
         System.out.println(maxCount);
         System.out.println();
    }

}

/**
 * JVM运行时
 * 
 * @param serverRuntime
 * @throws AttributeNotFoundException
 * @throws InstanceNotFoundException
 * @throws MBeanException
 * @throws ReflectionException
 * @throws IOException
 */
public void getJVMRuntime(ObjectName serverRuntime)
        throws AttributeNotFoundException, InstanceNotFoundException,
        MBeanException, ReflectionException, IOException {
    // JVM运行时 JVMRuntime
    ObjectName JVMRuntime = getAttribute(serverRuntime, "JVMRuntime");
    // 名称 Name
    String name = getAttribute(JVMRuntime, "Name");
    // 堆大小最大值 HeapSizeMax
    Long heapSizeMax = getAttribute(JVMRuntime, "HeapSizeMax");
    // 返回当前JVM堆中空闲内存数，单位时字节 HeapFreePercent
    Integer heapFreePercent = getAttribute(JVMRuntime, "HeapFreePercent");
    // 当前堆的总空间
    Long heapSizeCurrent = getAttribute(JVMRuntime, "HeapSizeCurrent");
    // 当前堆已使用的空间 HeapFreeCurrent
    Long heapFreeCurrent = getAttribute(JVMRuntime, "HeapFreeCurrent");
    getAttribute(JVMRuntime, "HeapFreeCurrent");

    // TODO
    System.out.println(name);
    System.out.println(byteToMB(heapSizeMax));
    System.out.println(heapFreePercent);
    System.out.println(byteToMB(heapSizeCurrent));
    System.out.println(byteToMB(heapFreeCurrent));

}

/**
 * 服务器运行时
 * 
 * @param serverRuntime
 * @throws AttributeNotFoundException
 * @throws InstanceNotFoundException
 * @throws MBeanException
 * @throws ReflectionException
 * @throws IOException
 */
public void getServerRuntime(ObjectName serverRuntime)
        throws AttributeNotFoundException, InstanceNotFoundException,
        MBeanException, ReflectionException, IOException {
	
    // 服务名称 Name
    String name = getAttribute(serverRuntime, "Name");
    // 返回服务器激活时间ActivationTime
    Long activationTime = getAttribute(serverRuntime, "ActivationTime");

    Date date = new Date(activationTime);
    String time = formatDate(date, "yyyy/MM/dd HH:mm:ss");
    // 返回当前服务器监听连接的端口 ListenPort
    Integer listenPort = getAttribute(serverRuntime, "ListenPort");
    // 返回当前服务器监听连接的IP地址 ListenAddress
    String listenAddress = getAttribute(serverRuntime, "ListenAddress");
    // 状态 State
    String state = getAttribute(serverRuntime, "State");
    // 应用服务器的健康状态 HealthState
    HealthState healthState = (HealthState) connection.getAttribute(
            serverRuntime, "HealthState");
    // 当前打开的Socket数量 OpenSocketsCurrentCount
    Integer openSocketsCurrentCount = getAttribute(serverRuntime,
            "OpenSocketsCurrentCount");
    // 打开的Socket的总数 SocketsOpenedTotalCount
    Long socketsOpenedTotalCount = getAttribute(serverRuntime,
            "SocketsOpenedTotalCount");
    // 当前连接数 TODO


    System.out.println(healthState.getState());
    System.out.println(healthState.getMBeanName());
    System.out.println(healthState.getMBeanType());
    System.out.println(healthState.getSubsystemName());
    // TODO
	System.out.println(name);
	System.out.println(time);
	System.out.println(listenPort);
	System.out.println(listenAddress);
	System.out.println(state);
	//System.out.println(healthState.getState());
	System.out.println(openSocketsCurrentCount);
	System.out.println(socketsOpenedTotalCount);

}

/**
 * web应用程序运行时组件
 * 
 * @param serverRuntime
 * @throws AttributeNotFoundException
 * @throws InstanceNotFoundException
 * @throws MBeanException
 * @throws ReflectionException
 * @throws IOException
 */
public void getWebAppComponentRuntime(ObjectName serverRuntime)
        throws AttributeNotFoundException, InstanceNotFoundException,
        MBeanException, ReflectionException, IOException {
    // 获取web应用程序运行时组件
    ObjectName[] applicationRuntimes = getAttribute(serverRuntime,
            "ApplicationRuntimes");
    for (int i = 0; i < applicationRuntimes.length; i++) {
        ObjectName applicationRuntime = applicationRuntimes[i];
        ObjectName[] componentRuntimes = getAttribute(applicationRuntime,
                "ComponentRuntimes");
        for (int j = 0; j < componentRuntimes.length; j++) {
            ObjectName componentRuntime = componentRuntimes[j];
            String componentType = getAttribute(componentRuntime, "Type");
            // web应用程序运行时组件 WebAppComponentRuntime
            if (componentType.equals("WebAppComponentRuntime")) {
                // 单元名 Name
                String name = getAttribute(componentRuntime, "Name");
                // 部署状态 DeploymentState
                Integer deploymentState = getAttribute(componentRuntime,
                        "DeploymentState");
                // 当前打开的Session数 OpenSessionsCurrentCount
                Integer openSessionsCurrentCount = getAttribute(
                        componentRuntime, "OpenSessionsCurrentCount");
                // Web应用程序名称
                // 打开的Session最高数 OpenSessionsHighCount
                Integer openSessionsHighCount = getAttribute(
                        componentRuntime, "OpenSessionsHighCount");
                // 打开的Session的总数 SessionsOpenedTotalCount
                Integer sessionsOpenedTotalCount = getAttribute(
                        componentRuntime, "SessionsOpenedTotalCount");
                // 应用状态 Status
                String status = getAttribute(componentRuntime, "Status");

                // TODO
                System.out.println("name　= " + name + ", status = "
                        + deploymentState);
                System.out.println(openSessionsCurrentCount);
                System.out.println(openSessionsHighCount);
                System.out.println(sessionsOpenedTotalCount);
                System.out.println(status);
                System.out.println();
            }
        }
    }
}

/**
 * 线程池运行时
 * 
 * @throws IOException
 * @throws ReflectionException
 * @throws MBeanException
 * @throws InstanceNotFoundException
 * @throws AttributeNotFoundException
 */
public void getThreadPoolRuntime(ObjectName serverRuntime)
        throws AttributeNotFoundException, InstanceNotFoundException,
        MBeanException, ReflectionException, IOException {
    // 获取线程池运行时
    ObjectName threadPoolRuntime = getAttribute(serverRuntime,
            "ThreadPoolRuntime");
    // 暂停的 Suspended
    Boolean suspended = (Boolean) getAttribute(threadPoolRuntime,
            "Suspended");
    // 待处理用户请求计数 PendingUserRequestCount
    Integer pendingUserRequestCount = getAttribute(threadPoolRuntime,
            "PendingUserRequestCount");
    // 占用线程计数 HoggingThreadCount
    Integer hoggingThreadCount = getAttribute(threadPoolRuntime,
            "HoggingThreadCount");
    // 队列长度 QueueLength
    Integer queueLength = getAttribute(threadPoolRuntime, "QueueLength");

    System.out.println(suspended);
    System.out.println(pendingUserRequestCount);
    System.out.println(hoggingThreadCount);
    System.out.println(queueLength);
}

/**
 * 获取weblogic属性参数
 * 
 * @param objectName
 * @param name
 * @return
 */
@SuppressWarnings("unchecked")
public static <T> T getAttribute(ObjectName objectName, String name) {
    Object obj = null;
    try {
        obj = connection.getAttribute(objectName, name);
    } catch (Exception e) {
        // TODO
        e.printStackTrace();
    }
    return (T) obj;
}

/**
 * 日期格式转换
 * 
 * @param date
 * @param format
 * @return
 */
public static String formatDate(Date date, String format) {
    DateFormat df = new SimpleDateFormat(format);
    return df.format(date);
}

/**
 * 字节转换成MB
 * 
 * @param bytes
 * @return
 */
public static String byteToMB(long bytes) {
    double mb = (double) bytes / 1024 / 1024;
    DecimalFormat df = new DecimalFormat("#.00");
    return df.format(mb);
}

// TODO
public static void main(String[] args) throws Exception {
    JmxWeblogicDemo weblogic = new JmxWeblogicDemo();
    initConnection();

    ObjectName serverRuntime = getAttribute(runtimeService, "ServerRuntime");

    weblogic.getConnectorServiceRuntime(serverRuntime);
    System.out.println("=================Status===================");
    weblogic.getServerRuntime(serverRuntime);
    System.out.println("=================Queue===================");
    weblogic.getExecuteQueueRuntimes(serverRuntime);
    System.out.println("=================JVM===================");
    weblogic.getJVMRuntime(serverRuntime);
    System.out.println("=================JDBC===================");
    weblogic.getJDBCDataSourceRuntime(serverRuntime);
    System.out.println("=================POOL===================");
    weblogic.getThreadPoolRuntime(serverRuntime);
    System.out.println("==================APPLICATION==================");
    weblogic.getWebAppComponentRuntime(serverRuntime);
    
}
}