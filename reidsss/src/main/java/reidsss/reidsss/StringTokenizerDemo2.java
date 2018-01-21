package reidsss.reidsss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class StringTokenizerDemo2 {

	public static void testField(String keys, List<String> values) {

		if (null == values && values.size() == 0) {
			return;
		}
		long begin = System.currentTimeMillis();
		long vv = 0;
		List<TestvO> list = new ArrayList<>(values.size());

		TestvO current = new TestvO();

		Iterator<String> iter = values.iterator();
		for (int i = 0; iter.hasNext(); i++) {

			long begin2 = System.currentTimeMillis();
			StringTokenizer st = new StringTokenizer(iter.next(), ";");
			long begin3 = System.currentTimeMillis();
			vv += begin3 - begin2;
			for (int j = 0; st.hasMoreTokens(); j++) {

				String value = st.nextToken();
				if (j == 0) {
					setValue(current, j, value);
				} else {
					if (j > 4) {
						continue;
					}
					setValue2(current, j, value);
				}

			}

			if ((i + 1) % 100 == 0) {
				// System.out.println(current.getIp());
				caacl(current);

				list.add(current);
				current = new TestvO();
			}

		}

		long end = System.currentTimeMillis();
		// System.out.println((end - begin) + "==" + list.size());

		// System.out.println(vv);
	}

	private static void caacl(TestvO current) {

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

	// String a =
	// "34343333.00;3444333.00;5555.0000;8888.000;9999444.000;hstomerdr;afasjflafjajafj,afasjflafjajaf";

	private static void setValue(TestvO vo, int i, String value) {
		switch (i) {

		case 0: {
			vo.setCaps(Double.valueOf(value));
			return;
		}
		case 1: {
			vo.setQuest(Double.valueOf(value));
			return;
		}
		case 2: {
			vo.setTcaps(Double.valueOf(value));
			return;
		}
		case 3: {
			vo.setSucReest(Double.valueOf(value));
			return;
		}
		case 4: {
			vo.setTtool(Double.valueOf(value));
			return;
		}
		case 5: {
			vo.setHostname(value);
			return;
		}
		case 6: {
			vo.setItemName(value);
		}
		}
	}

	//// String a =
	// "34343333.00;3444333.00;5555.0000;8888.000;9999444.000;hstomerdr;afasjflafjajafj,afasjflafjajaf";

	private static void setValue2(TestvO vo, int i, String value) {
		switch (i) {

		case 0: {

			vo.setCaps(vo.getCaps() + Double.valueOf(value));
			return;
		}
		case 1: {
			vo.setQuest(vo.getQuest() + Double.valueOf(value));
			return;
		}
		case 2: {
			vo.setTcaps(vo.getTcaps() + Double.valueOf(value));
			return;
		}
		case 3: {
			vo.setSucReest(vo.getSucReest() + Double.valueOf(value));
			return;
		}
		case 4: {
			vo.setTtool(vo.getTtool() + Double.valueOf(value));
			return;
		}

		}
	}

}