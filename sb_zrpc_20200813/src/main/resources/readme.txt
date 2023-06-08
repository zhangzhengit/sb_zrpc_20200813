
==========================使用rpc=======================
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
	
==========================使用rpc分布式事务=======================
配置同上，
1 新建
	@Component
	public class RC {
		@Autowired
		R r;
	
		@ZDistributedTransaction(id = "createOne", steps = {}, name = { "createOne", "createOne"})
		public Test1Entity createOne() {
			final Test1Entity createOne = this.r.createOne("AA");
			final Test1Entity createOne2 = this.r.createOne("BB");
			//final int n = 20 / 0;
			return createOne;
		}
		
	}
	
	# @ZDistributedTransaction
		id = 方法名
		name = 所有方法名内的@ZDTLocal标记的远程方法提供者的名称
		带有此注解的方法会被aop进行控制，如果此方法异常了（@ZDTLocal提供者至少一个异常了）
		则通知全部的提供者rollback，如果全部提供者都成功了，再通知全部提供者进行commit
		
	
2	新建 interface R

 	@ZRPCRemoteMethodAnnotation(serviceName = "sb_zrpc_client_TEST_1")
	public interface R {
	
		@ZRPCRemoteDT
		public void chufa(int x);
	
		@ZRPCRemoteDT
		public Test1Entity createOne(final String name);
		
	}	

	# @ZRPCRemoteDT
		表示此方法是一个分布式事务中的一个步骤，
		带有此注解的方法，在调用远程方法时 attachMap中会多一个
		ZRPCProtocolAttachEnum.DISTRIBUTED_TRANSACTION值
		

3 新建 class P 
	@ZRPCComponent
	public class P {
	
		@Autowired
		Test1Repository test1Repository;
		@Autowired
		private Test1Service test1Service;
	
		@ZDTLocal
		public Test1Entity createOne(final String name) {
			final Test1Entity entity = new  Test1Entity();
			entity.setName(name);
			final Test1Entity save = this.test1Repository.save(entity);
	
			return save;
		}
		
		# @ZDTLocal
			表示此方法是一个分布式事务的提供者，带有此注解的
			方法会使用编程式事务控制，方法执行后先不commit也不rollback,
			而是等所有的一个分布式事务中的@ZDTLocal方法都完成（返回结果给消费者）以后
			再看几个@ZDTLocal方法如果都成功则消费者发送commit给所有@ZDTLocal方法，
			只要有一个@ZDTLocal方法异常了，则发送异常消息个消费者，
			然后消费者发送rollback消费给所有@ZDTLocal方法来执行rollback.
	}

4 API 使用	rc.createOne() 即可完成分布式事务
	







	
	
	