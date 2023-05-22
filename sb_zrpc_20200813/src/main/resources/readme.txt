
1 启动sb_zrpc 为server

2 新建工程A B  maven加入依赖 sb_zrpc_client

3 A为rpc提供者， B为消费者 配置好application.properties的中 zprc项：
	前两项配置为 sb_zrpc的ip和端口
 	zrpc.server-host=192.168.1.14
	zrpc.server-port=9995
	zrpc.scan-package-name-set=com
	zrpc.service-name=A
	
 	
4 A B 加入注解＠@EnableZRPC

5 A 新建class KK 加入注解@ZRPCComponent，写一个远程方法，如：
	public String test(){
		return "OK";
	}

6 B 新建interface  II 加入注解ZRPCRemoteMethodAnnotation，生命一个方法同第5步：
	interface 加入注解：
	@ZRPCRemoteMethodAnnotation(serviceName = "A")
	serviceName为第3步配置的zrpc.service-name
	String test();

7 B	@RestControllerAdvice要加入捕捉 ZRPCRemoteMethodException异常，这个是远程方法
 	执行过程中抛出的异常
 	
8 B 新建controller 加入 @Autowired II ii;
	使用 ii.test()来调用A提供的远程方法
	
	
	