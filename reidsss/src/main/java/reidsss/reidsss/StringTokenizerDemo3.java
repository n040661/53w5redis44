package reidsss.reidsss;

import java.util.Map.Entry;
import java.util.StringTokenizer;

public class StringTokenizerDemo3 {

	public static void test(Entry<String, String> mapValues) {
		long begin = System.currentTimeMillis();

		TestvO current = new TestvO();

		for (int i = 0; i < 3000000; i++) {

			StringTokenizer st = new StringTokenizer(mapValues.getValue(), ";");
			for (int j = 0; st.hasMoreTokens(); j++) {

				String value = st.nextToken();
				setValue(current, j, value);
			}

			if ((i + 1) % 100 == 0) {
				// System.out.println(current.getIp());
				caacl(current);
				current = new TestvO();
			}
		}

		long end = System.currentTimeMillis();
		System.out.println((end - begin) + "==" + list.size());

	}

	public static void caacl(TestvO current) {

		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));
		current.setCaps(Math.round(current.getCaps() / current.getQuest()));

	}

	public static void setValue(TestvO vo, int index, String value) {

		if (index == 0) {
			vo.setCaps(vo.getCaps() + Double.valueOf(value));
		} else if (index == 1) {
			vo.setQuest(vo.getQuest() + Double.valueOf(value));
		} else if (index == 2) {
			vo.setTcaps(vo.getTcaps() + Double.valueOf(value));
		} else if (index == 3) {
			vo.setSucReest(vo.getSucReest() + Double.valueOf(value));
		} else if (index == 3) {
			vo.setTtool(vo.getTtool() + Double.valueOf(value));
		}
		//
		// case 7: {
		// vo.setHostname(value);
		// return;
		// }
		// case 8: {
		// vo.setItemName(value);
		// }

	}

	public static void setValue2(TestvO vo, int i, String value) {
		switch (i) {

		case 2: {

			vo.setCaps(vo.getCaps() + Double.valueOf(value));
			return;
		}
		case 3: {
			vo.setQuest(vo.getQuest() + Double.valueOf(value));
			return;
		}
		case 4: {
			vo.setTcaps(vo.getTcaps() + Double.valueOf(value));
			return;
		}
		case 5: {
			vo.setSucReest(vo.getSucReest() + Double.valueOf(value));
			return;
		}
		case 6: {
			vo.setTtool(vo.getTtool() + Double.valueOf(value));
			return;
		}

		}
	}

}