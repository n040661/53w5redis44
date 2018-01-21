package reidsss.reidsss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ZParams;

/**
 * redis常用api
 * 
 * @author Kazz
 *
 */
public class RedisDemo5 implements Runnable {

	private static JedisPool jedisPool = null;

	static int c = 0;

	public static void main(String[] args) throws Exception {

		// 这个是最简单的redis连接示例，不过不推荐，推荐使用数据库连接池
		// Jedis jedis = new Jedis("192.168.8.128", 6379);// 连接 Redis 服务
		// jedis.auth("123456"); // 设置密码
		// System.out.println("Server is running: " + jedis.ping());//
		// 查看服务是否运行

		init();
		// string();

		// hash2();

		// hash3();

		getIps();

		System.out.println(c);

		// list();
		//
		// set();
		//
		// sets();
		//
		// hash();
		//
		// zset();
		//
		// zsets();
		//
		// publisher();
		//
		// subscribe();
	}

	private static List<String> getYears() {
		List<String> years = new ArrayList<>();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyMMdd");
			Date dateString = formatter.parse("2017-01-01 00:00:00");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateString);

			for (int i = 0; i < 367; i++) {

				years.add(formatter2.format(calendar.getTime()));

				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return years;

	}

	private static void getIps() {

		List<String> ips = new ArrayList<>();

		for (int j = 0; j < 50; j++) {
			String aa = "00" + j;
			if (j > 9 && j < 100) {
				aa = "0" + j;
			}
			ips.add("000000000" + aa);
		}

		Jedis jedis = jedisPool.getResource();

		long start = System.currentTimeMillis();
		List<TestvO> resultValues = new ArrayList<>();

		List<String> ipInterfaceIds = new ArrayList<>();
		List<String> days = get39Days();
		for (String ip : ips) {
			queryScanResult(jedis, ip, ipInterfaceIds, resultValues, "0", days);
		}

		System.out.println(resultValues.size());
		System.out.println(System.currentTimeMillis() - start);

		// for (TestvO vo : resultValues) {
		// System.out.println(vo.toString());
		// }

	}

	private static void queryScanResult(Jedis jedis, String ip, List<String> ipInterfaceIds, List<TestvO> resultValues,
			String cursor, List<String> days) {

		// jedis.select(1);
		// ScanParams scan = new ScanParams();
		// scan.count(100);
		ScanResult<Map.Entry<String, String>> hscanResult = jedis.hscan(ip, cursor);
		// System.out.println("0==>" + ip + "=" + cursor);

		List<Map.Entry<String, String>> scanResult = hscanResult.getResult();
		for (Map.Entry<String, String> mapentry : scanResult) {
			ipInterfaceIds.add(mapentry.getKey() + ":" + ip);
		}

		// System.out.println("0==>" + ipInterfaceIds.size());
		// TODO
		// jedis.select(0);
		if (ipInterfaceIds.size() == 100) {
			queryListInfo(jedis, ipInterfaceIds, resultValues, days);
			ipInterfaceIds.clear();
		}

		// System.out.println("0".equals(hscanResult.getStringCursor()));

		// 返回0 说明遍历完成
		if (!"0".equals(hscanResult.getStringCursor())) {

			queryScanResult(jedis, ip, ipInterfaceIds, resultValues, cursor, days);
		}

	}

	private static void queryListInfo2(Jedis jedis, List<String> ipInterfaceIds, List<TestvO> resultValues) {

		for (String key : ipInterfaceIds) {
			ScanResult<Map.Entry<String, String>> hscanResult = jedis.hscan(key, "0");
			if (!"0".equals(hscanResult.getStringCursor())) {

				// queryListInfo2(jedis, key, ipInterfaceIds, resultValues, cursor);
			}
		}

	}

	private static void queryListInfo(Jedis jedis, List<String> ipInterfaceIds, List<TestvO> resultValues,
			List<String> days) {

		// System.err.println("queryListInfo");
		Pipeline p = jedis.pipelined();

		Map<String, Response<List<String>>> responses = new HashMap<>(ipInterfaceIds.size());

		String[] SS = get90Days();
		for (String key : ipInterfaceIds) {
			// System.out.println(key);
			c++;
			// responses.put(key, p.hmget(key, get90Days())); //

			responses.put(key, p.hmget(key, SS));

		}

		p.sync();

		// p.dump(key)

		// System.err.println("queryListInfo2>>>>" + ipInterfaceIds.size());
		// handlerDatas(resultValues, responses, days);
	}

	private static void handlerDatas(List<TestvO> resultValues, Map<String, Response<Map<String, String>>> responses,
			List<String> days) {

		Collection<Response<Map<String, String>>> values = responses.values();
		Entry<String, String> entry = null;
		for (Response<Map<String, String>> valus : values) {
			Map<String, String> map = valus.get();
			Iterator<Map.Entry<String, String>> list = map.entrySet().iterator();

			for (int i = 0; list.hasNext(); i++) {

				entry = list.next();
				if (!days.contains(entry.getKey())) {
					continue;
				}
				// System.err.println("queryListInfo3");
				TestvO current = new TestvO();
				StringTokenizer st = new StringTokenizer(entry.getValue(), ";");
				for (int j = 0; st.hasMoreTokens(); j++) {

					String value = st.nextToken();
					// StringTokenizerDemo3.setValue(current, j, value);
				}

				// System.err.println("queryListInfo3");
				// if (values.size() == i) {
				// // System.out.println(current.getIp());
				// StringTokenizerDemo3.caacl(current);
				// resultValues.add(current);
				// // System.err.println(current.toString());
				// current = new TestvO();
				// }
			}
		}
	}

	// hstomerdr; afasjflafjajafj,afasjflafjajaf
	// 3434333399.00;3434333399.00|3434333399.00;3434333399.00;3434333399.00

	// String a = "3434333399.00;"; 172m
	// String a = "3434333399.00;3434333399.00;3434333399.00;3434333399.00"; 458M
	// 50 node

	// String a = "3434333399.00;3434333399.00;3434333399.00"; node 90 810m
	private static void hash2() throws ParseException {

		String a = "3434333.00;3434333.00;3434333.00;3434333.00;3434333.00";

		Jedis jedis = jedisPool.getResource();

		jedis.flushDB(); // 清空数据库

		// jedis.select(1);

		Map<String, String> infos = new HashMap<>();
		// interceId
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 500; j++) {
				// infos.put(i + ":" + j + "0000000000", a);
				// Integer.toBinaryString(strChar[i])
				String aa = "00" + j;
				if (j > 9 && j < 100) {
					aa = "0" + j;
				}
				String xx = i < 10 ? "0" + i : i + "";

				String ip = "000000000" + aa;

				infos.put(xx + ":" + ip, a);

				jedis.hset(ip, xx, "1");
			}
		}

		System.out.println("size interf id" + infos.size());

		Iterator<Entry<String, String>> iter = null;
		Entry<String, String> values = null;
		// jedis.select(0);

		List<String> years = getYears();
		for (String year : years) {

			iter = infos.entrySet().iterator();
			int j = 0;
			for (int i = 0; iter.hasNext(); i++) {

				values = iter.next();
				// String[] ss = values.getValue().split("\\|");
				jedis.hset(values.getKey(), year + "" + 1, values.getValue());
				// jedis.hset(values.getKey(), year + "" + 2, ss[1]);

				// jedis.set(values.getKey() + "" + year, values.getValue());

			}

		}

		System.out.println("end ---all");
		jedis.close();

	}

	private static void hash3() {
		Jedis jedis = jedisPool.getResource();

		get90Days();

		long ss = System.currentTimeMillis();
		// Set<String> keys = jedis.hscan(key, cursor)("*");

		long s3 = System.currentTimeMillis();

		System.out.println("get all keys : " + (s3 - ss));

		long allTime = 0;
		long allTime2 = 0;

		long s88 = System.currentTimeMillis();
		Set<String> keys = jedis.keys("*");
		String[] keyArray = keys.toArray(new String[] {});
		int size = keyArray.length;
		long s4 = System.currentTimeMillis();
		Pipeline p = jedis.pipelined();

		Response<List<String>> list = null;
		for (int i = 0; i < 20000; i++) {

			p.hgetAll(keyArray[i]);// (keyArray[i]);

			// p.hgetAll(keyArray[i]);
			if ((i + 1) % 100 == 0) {
				//
				p.sync();
				p.clear();
			}

		}

		// List<String> lists = list.get();
		//
		// for (String ssz : lists) {
		// System.out.println(ssz);
		// }

		long s5 = System.currentTimeMillis();
		allTime += s5 - s4;

		System.out.println("get hmget : " + allTime + "=====" + allTime2);

	}

	private static String[] get90Days() {
		List<String> years = getYears();
		List<String> subList = years.subList(0, 90);
		return subList.toArray(new String[] {});
	}

	private static List<String> get39Days() {
		List<String> years = getYears();
		return years.subList(0, 90);
	}

	private static void hash4() {
		Jedis jedis = jedisPool.getResource();
		Pipeline p = jedis.pipelined();
		// Map<String, String> getMap = jedis.hgetAll("hash1"); // 从redis中取回来
		// System.out.println("从redis中取回的hash1散列：" + getMap.toString());
		// System.out.println();
		List<String> years = getYears();
		List<String> subList = years.subList(0, 90);
		String[] arrays = subList.toArray(new String[] {});

		long ss = System.currentTimeMillis();
		Set<String> keys = jedis.keys("*");
		long s3 = System.currentTimeMillis();

		System.out.println("get all keys : " + (s3 - ss));

		// Map<String, Response<Map<String, String>>> responses = new HashMap<String,
		// Response<Map<String, String>>>(
		// keys.size());

		long allTime = 0;
		long allTime2 = 0;
		//
		// for (String key : keys) {
		// responses.put(key, p.hgetAll(key));
		// }
		//
		// p.sync();

		// for (String k : responses.keySet()) {
		// // result.put(k, responses.get(k).get());
		// }
		long s88 = System.currentTimeMillis();
		System.out.println("get hmget : " + (s3 - s88));
		p = jedis.pipelined();

		long s4 = System.currentTimeMillis();
		System.out.println("get hmget : " + (s4 - s88));
		for (String key : keys) {

			// p.hgetAll(key);

			p.hmget(key, arrays);
			// for (String ssz : subList) {
			// List<String> hmget = jedis.hmget(key, ssz);
			//
			// }

			// System.out.println("get hmget : " + (s5 - s4));
			// long s6 = System.currentTimeMillis();
			// StringTokenizerDemo2.testField(key, hmget);
			// long s7 = System.currentTimeMillis();
			// allTime2 += s7 - s6;
		}

		p.sync();
		long s5 = System.currentTimeMillis();
		allTime += s5 - s4;

		System.out.println("get hmget : " + allTime + "=====" + allTime2);

	}

	/**
	 * 初始化redis连接池
	 */
	private static void init() {
		JedisPoolConfig config = new JedisPoolConfig(); // Jedis连接池
		config.setMaxIdle(8); // 最大空闲连接数
		config.setMaxTotal(8);// 最大连接数
		config.setMaxWaitMillis(1000); // 获取连接是的最大等待时间，如果超时就抛出异常
		config.setTestOnBorrow(false);// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnReturn(true);
		jedisPool = new JedisPool(config, "localhost", 6379, 90000000, "beijing"); // 配置、ip、端口、连接超时时间、密码、数据库编号（0~15）
	}

	/**
	 * string类型的基本操作，string是redis的最基本数据类型，很多操作都是其他数据类型能用的，如del、exists、expire
	 * 
	 * @throws Exception
	 * 
	 */
	private static void string() throws Exception {

		Jedis jedis = jedisPool.getResource();

		/*
		 * ShardedJedis jd = new ShardedJedis(); jd.hset(key, field, value);
		 */

		jedis.select(0);
		jedis.flushDB(); // 清空数据库

		jedis.set("testString", "123"); // 往redis中放入字符串
		System.out.println("从redis中获取刚刚放进去的testString：" + jedis.get("testString"));

		jedis.incr("testString"); // 自增，不存在testInt则自增结果是1，如果不是字符串，自增会报JedisDataException
		System.out.println("从redis中获取自增后的testString：" + jedis.get("testString"));

		jedis.decr("testString"); // 自减，效果同自增
		System.out.println("从redis中获取自减后的testString：" + jedis.get("testString"));
		// incrby方法可以自定要增加多少

		jedis.append("testString", "456abcd"); // 在后面追加
		System.out.println("从redis中获取追加后的testString：" + jedis.get("testString"));

		String sub = jedis.substr("testString", 2, 6); // 切割字符串
		System.out.println("substr方法的返回值：" + sub);
		System.out.println("从redis中获取切割后的testString：" + jedis.get("testString")); // 可以看出，substr方法并不会破坏原有值，只是取出来加工而已

		jedis.rename("testString", "newString"); // 字段改名，值不会变
		System.out.println("testString改名成newString后，值为：" + jedis.get("newString"));

		String type = jedis.type("newString");// 获取其数据类型
		System.out.println("newString的数据类型是：" + type);

		long length = jedis.strlen("newString"); // 获取字符串长度
		System.out.println("newString的字符串长度为：" + length);

		jedis.set("testString6", "哈哈");
		jedis.set("testString7", "呵呵");
		jedis.set("testString8", "helloword");
		jedis.set("testString99", "SMSP");
		Set<String> keys = jedis.keys("*"); // 获取所有符合条件的键
		System.out.println("返回redis中所有的键：" + keys);
		keys = jedis.keys("*String?");
		System.out.println("返回redis中所有正则符合*String?的键：" + keys);

		jedis.del("testString"); // 字符串删除
		System.out.println("从redis删除testInt后，testInt是否还存在：" + jedis.exists("testString"));
		System.out.println();

		jedis.set("testString2", "你好啊！！！");
		jedis.expire("testString2", 2); // 设置有效期，单位是秒
		System.out.println("从redis中获取testString2的值为：" + jedis.get("testString2"));
		Thread.sleep(3000);
		System.out.println("3秒后从redis中获取testString2的值为：" + jedis.get("testString2")); // 过期了，会找不到该字段，返回null
		// ttl方法可以返回剩余有效时间，expire如果方法不指定时间，就是将该字段有效期设为无限
		System.out.println();
		System.out.println();
		jedis.close();
	}

	/**
	 * list类的基本操作，有序可重复
	 * 
	 */
	private static void list() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB(); // 清空数据库

		// 列表的插入与获取(可以重复)
		jedis.lpush("testList", "Redis"); // 从左边插入
		jedis.lpush("testList", "Mongodb");
		jedis.lpush("testList", "Mysql");
		jedis.lpush("testList", "Mysql");
		jedis.rpush("testList", "DB2"); // 从右边插入

		List<String> list = jedis.lrange("testList", 0, -1); // 从左到右遍历，3个参数分别是，key，开始位置，结束位置（-1代表到最后）
		for (int i = 0; i < list.size(); i++) {
			System.out.printf("从redis中获取刚刚放进去的testList[%d]: %s\n", i, list.get(i));
		}

		System.out.println();
		String lpop = jedis.lpop("testList"); // 删掉最左边的那个
		String rpop = jedis.rpop("testList"); // 删掉最右边的那个
		System.out.printf("被删的左边元素是：%s，被删的右边元素是：%s\n", lpop, rpop);

		list = jedis.lrange("testList", 0, -1);
		for (int i = 0; i < list.size(); i++) {
			System.out.printf("从redis中获取被删除后的testList[%d]: %s\n", i, list.get(i));
		}

		System.out.println();
		jedis.ltrim("testList", 1, 2); // 裁剪列表，三个参数分别是，key，开始位置，结束位置
		list = jedis.lrange("testList", 0, -1);
		for (int i = 0; i < list.size(); i++) {
			System.out.printf("从redis中获取被裁剪后的testList[%d]: %s\n", i, list.get(i));
		}

		jedis.del("testList"); // 删除列表
		System.out.println("从redis删除testList后，testList是否还存在：" + jedis.exists("testList"));
		System.out.println();
		System.out.println();
		jedis.close();
	}

	/**
	 * 集合类型的基本操作，无序不重复
	 */
	private static void set() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB(); // 清空数据库

		jedis.sadd("testSet", "lida", "wch", "chf", "lxl", "wch"); // 添加元素，不可重复

		Set<String> set = jedis.smembers("testSet"); // 获取集合中的全部元素
		System.out.println("从testSet中获取的元素：" + set);

		long length = jedis.scard("testSet"); // 求集合的长度
		System.out.println("\n获取testSet的长度：" + length);
		System.out.println();

		jedis.srem("testSet", "wch"); // 从testSet移除wch
		set = jedis.smembers("testSet");
		System.out.println("从testSet中获取移除后的的元素：" + set);
		System.out.println();

		boolean exist = jedis.sismember("testSet", "lida"); // 判断元素是否包含在该集合中
		System.out.println("检查lida是否包含在testSet中：" + exist);
		System.out.println();

		String spop = jedis.spop("testSet");// 随机的移除spop中的一个元素，并返回它
		System.out.println("testSet中被随机移除的元素是：" + spop);
		System.out.println();

		jedis.del("testSet"); // 删除整个集合
		System.out.println("删除后，testSet是否还是存在：" + jedis.exists("testSet"));
		System.out.println();
		System.out.println();

		jedis.close();
	}

	/**
	 * 集合之间的运算，交集、并集、差集
	 */
	private static void sets() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB(); // 清空数据库
		jedis.sadd("set1", "a", "b", "c", "d");
		jedis.sadd("set2", "b", "c", "e");

		Set<String> set = jedis.sdiff("set1", "set2"); // 求两个集合的差集（只会返回存在于1，但2不存在的）
		System.out.println("求出两个集合之间的差集：" + set); // 会输出a和d
		// 还有一个sdiffstore的api，可以把sdiff的计算结果赋值到另一个set中，下面的交集和并集也类似
		System.out.println();

		set = jedis.sinter("set1", "set2"); // 求两个集合的交集
		System.out.println("求出两个集合之间的交集：" + set); // 会输出b和c
		System.out.println();

		set = jedis.sunion("set1", "set2"); // 求两个集合的并集
		System.out.println("求出两个集合之间的并集：" + set);
		System.out.println();
		System.out.println();

		jedis.close();
	}

	/**
	 * 散列的基本操作,键值对里面还有键值对，经常用来存储多个字段信息，也可以理解为存放一个map，散列是redis的存储原型
	 */
	private static void hash() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB(); // 清空数据库

		Map<String, String> map = new HashMap<String, String>();
		map.put("k1", "v1");
		map.put("k2", "v2");
		map.put("k3", "v3");
		map.put("k4", "123");
		jedis.hmset("hash1", map); // 存放一个散列

		Map<String, String> getMap = jedis.hgetAll("hash1"); // 从redis中取回来
		System.out.println("从redis中取回的hash1散列：" + getMap.toString());
		System.out.println();

		List<String> hmget = jedis.hmget("hash1", "k1", "k3"); // 从散列中取回一个或多个字段信息
		System.out.println("从hash1散列中两个字段来看看：" + hmget);
		System.out.println();

		jedis.hdel("hash1", "k1"); // 删除散列中的一个或者多个字段
		getMap = jedis.hgetAll("hash1");
		System.out.println("从redis中取回的被删除后的hash1散列：" + getMap);
		System.out.println();

		long length = jedis.hlen("hash1"); // 求出集合的长度
		System.out.println("散列hash1的长度为：" + length);
		System.out.println();

		boolean exists = jedis.hexists("hash1", "k5"); // 判断某个字段是否存在于散列中
		System.out.println("k5字段是否存在于散列中：" + exists);
		System.out.println();

		Set<String> keys = jedis.hkeys("hash1"); // 获取散列的所有字段名
		System.out.println("hash1的所有字段名：" + keys);
		System.out.println();

		List<String> values = jedis.hvals("hash1"); // 获取散列的所有字段值，实质的方法实现，是用上面的hkeys后再用hmget
		System.out.println("hash1的所有字段值：" + values);
		System.out.println();

		jedis.hincrBy("hash1", "k4", 10); // 给散列的某个字段进行加法运算
		System.out.println("执行加法运行后的hash1散列：" + jedis.hgetAll("hash1"));
		System.out.println();

		jedis.del("hash1"); // 删除散列
		System.out.println("删除hash1后，hash1是否还存在redis中：" + jedis.exists("hash1"));
		System.out.println();
		System.out.println();

		jedis.close();
	}

	/**
	 * 有序集合的基本使用，zset是set的升级版，在无序的基础上，加入了一个权重，使其有序化<br/>
	 * 另一种理解，zset是hash的特殊版，一样的存放一些键值对，但这里的值只能是数字，不能是字符串<br/>
	 * zset广泛应用于排名类的场景
	 */
	private static void zset() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB(); // 清空数据库

		Map<String, Double> map = new HashMap<String, Double>();
		map.put("wch", 24.3); // 这里以小组成员的年龄来演示
		map.put("lida", 30.0);
		map.put("chf", 23.5);
		map.put("lxl", 22.1);
		map.put("wch", 24.3); // 这个不会被加入，应该重复了

		jedis.zadd("zset1", map); // 添加一个zset

		Set<String> range = jedis.zrange("zset1", 0, -1); // 从小到大排序，返回所有成员，三个参数：键、开始位置、结束位置（-1代表全部）
		// zrange方法还有很多衍生的方法，如zrangeByScore等，只是多了一些参数和筛选范围而已，比较简单，自己看看api就知道了
		System.out.println("zset返回的所有从小大到排序的成员：" + range);
		System.out.println("");

		Set<String> revrange = jedis.zrevrange("zset1", 0, -1); // 从大到小排序，类似上面的range
		System.out.println("zset返回的所有排序的成员：" + revrange);
		System.out.println("");

		long length = jedis.zcard("zset1"); // 求有效长度
		System.out.println("zset1的长度：" + length);
		System.out.println();

		long zcount = jedis.zcount("zset1", 22.1, 30.0); // 求出zset中，两个成员的排名之差，注意不是求长度，
		System.out.println("zset1中，22.1和30.0差了" + zcount + "名");
		System.out.println();

		long zrank = jedis.zrank("zset1", "wch"); // 求出zset中某成员的排位，注意第一是从0开始的
		System.out.println("wch在zset1中排名：" + zrank);
		System.out.println();

		double zscore = jedis.zscore("zset1", "lida"); // 获取zset中某成员的值
		System.out.println("zset1中lida的值为：" + zscore);
		System.out.println();

		jedis.zincrby("zset1", 10, "lxl"); // 给zset中的某成员做加法运算
		System.out.println("zset1中lxl加10后，排名情况为：" + jedis.zrange("zset1", 0, -1));
		System.out.println();

		jedis.zrem("zset1", "chf"); // 删除zset中某个成员
		// zrem还有衍生的zremByScore和zremByRank，分别是删除某个分数区间和排名区间的成员
		System.out.println("zset1删除chf后，剩下：" + jedis.zrange("zset1", 0, -1));
		System.out.println();

		jedis.close();
	}

	/**
	 * 有序集合的运算，交集、并集（最小、最大、总和）
	 */
	private static void zsets() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushDB(); // 清空数据库

		Map<String, Double> map1 = new HashMap<String, Double>();
		map1.put("wch", 24.3); // 这里以小组成员的年龄来演示
		map1.put("lida", 30.0);
		map1.put("chf", 23.5);
		map1.put("lxl", 22.1);

		Map<String, Double> map2 = new HashMap<String, Double>();
		map2.put("wch", 24.3);
		map2.put("lly", 29.6);
		map2.put("chf", 23.5);
		map2.put("zjl", 21.3);

		jedis.zadd("zset1", map1);
		jedis.zadd("zset2", map2);

		System.out.println("zset1的值有：" + jedis.zrangeWithScores("zset1", 0, -1));
		System.out.println("zset2的值有：" + jedis.zrangeWithScores("zset2", 0, -1));
		System.out.println();

		jedis.zinterstore("zset_inter", "zset1", "zset2"); // 把两个集合进行交集运算，运算结果赋值到zset_inter中
		System.out.println("看看两个zset交集运算结果：" + jedis.zrangeWithScores("zset_inter", 0, -1));

		jedis.zunionstore("zset_union", "zset1", "zset2");// 把两个集合进行并集运算，运算结果赋值到zset_union中
		System.out.println("看看两个zset并集运算结果：" + jedis.zrangeWithScores("zset_union", 0, -1));
		System.out.println("可以看出，zset的交集和并集计算，默认会把两个zset的score相加");

		ZParams zParams = new ZParams();
		zParams.aggregate(ZParams.Aggregate.MAX);
		jedis.zinterstore("zset_inter", zParams, "zset1", "zset2"); // 通过指定ZParams来设置集合运算的score处理，有MAX MIN
																	// SUM三个可以选择，默认是SUM
		System.out.println("看看两个zset交集max运算结果：" + jedis.zrangeWithScores("zset_inter", 0, -1));

		// zrangeWithScores返回的是一个Set<Tuple>类型，如果直接把这个集合打印出来，会把zset的key转成ascii码，看起来不直观，建议还是使用foreach之类的遍历会好看一些

		jedis.close();
	}

	/**
	 * 发布消息，类似于mq的生产者
	 */
	private static void publisher() {

		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000); // 休眠一下，让订阅者有充足的时间去连上
					Jedis jedis = jedisPool.getResource();
					jedis.flushAll();

					for (int i = 0; i < 10; i++) {
						jedis.publish("channel", "要发送的消息内容" + i); // 每隔一秒推送一条消息
						System.out.printf("成功向channel推送消息：%s\n", i);
						Thread.sleep(1000);
					}

					jedis.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();
	}

	/**
	 * 订阅消息，类似与mq的消费者
	 * 
	 */
	private static void subscribe() {
		Jedis jedis = jedisPool.getResource();
		jedis.flushAll();
		JedisListener listener = new JedisListener();
		listener.proceed(jedis.getClient(), "channel"); // 开始监听channel频道的消息
		// listener.unsubscribe(); //取消监听
		jedis.close();
	}

	/**
	 * 重写监听器的一些重要方法，JedisPubSub里面的这些回调方法都是空的，不重写就什么事都不会发生
	 * 
	 * @author Kazz
	 *
	 */
	private static class JedisListener extends JedisPubSub {

		/**
		 * 收到消息后的回调
		 */
		@Override
		public void onMessage(String channel, String message) {
			System.out.println("onMessage: 收到频道[" + channel + "]的消息[" + message + "]");
		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {
			System.out.println("onPMessage: channel[" + channel + "], message[" + message + "]");
		}

		/**
		 * 成功订阅频道后的回调
		 */
		@Override
		public void onSubscribe(String channel, int subscribedChannels) {
			System.out
					.println("onSubscribe: 成功订阅[" + channel + "]," + "subscribedChannels[" + subscribedChannels + "]");
		}

		/**
		 * 取消订阅频道的回调
		 */
		@Override
		public void onUnsubscribe(String channel, int subscribedChannels) {
			System.out.println(
					"onUnsubscribe: 成功取消订阅[" + channel + "], " + "subscribedChannels[" + subscribedChannels + "]");
		}

		@Override
		public void onPUnsubscribe(String pattern, int subscribedChannels) {
			System.out.println(
					"onPUnsubscribe: pattern[" + pattern + "]," + "subscribedChannels[" + subscribedChannels + "]");
		}

		@Override
		public void onPSubscribe(String pattern, int subscribedChannels) {
			System.out.println(
					"onPSubscribe: pattern[" + pattern + "], " + "subscribedChannels[" + subscribedChannels + "]");
		}

	}

	@Override
	public void run() {
		Jedis jedis = jedisPool.getResource();

		get90Days();

		long ss = System.currentTimeMillis();
		// Set<String> keys = jedis.hscan(key, cursor)("*");

		long s3 = System.currentTimeMillis();

		System.out.println("get all keys : " + (s3 - ss));

		long allTime = 0;
		long allTime2 = 0;

		long s88 = System.currentTimeMillis();
		Set<String> keys = jedis.keys("*");
		String[] keyArray = keys.toArray(new String[] {});
		int size = keyArray.length;
		long s4 = System.currentTimeMillis();
		Pipeline p = jedis.pipelined();

		Response<List<String>> list = null;
		for (int i = 0; i < 1; i++) {

			list = p.hvals(keyArray[i]);// (keyArray[i]);

			// p.hgetAll(keyArray[i]);
			if (i % 1000 == 0) {
				p.sync();
				p.clear();
			}

		}

		// List<String> lists = list.get();
		//
		// for (String ssz : lists) {
		// System.out.println(ssz);
		// }

		long s5 = System.currentTimeMillis();
		allTime += s5 - s4;

		System.out.println("get hmget : " + allTime + "=====" + allTime2);

	}
}
