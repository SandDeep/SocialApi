package com.til.ibeat.dao.sort.helper;

import java.util.Map;

public class GenericComparableHelper implements Comparable<GenericComparableHelper>{
	private final String key;
	private final Map<String, Object> mDetail;
	private final String sortKey;
	
	public GenericComparableHelper(String tag,Map<String, Object> mDetail,String sortKey) {
		this.key = tag;
		this.mDetail = mDetail;
		this.sortKey = sortKey;
	}


	public Map<String, Object> getmDetail() {
		return mDetail;
	}

	@Override
	public int compareTo(GenericComparableHelper o) {
		Long count1  = 0l;
		Object obj = this.mDetail.get(this.sortKey);
		if (obj != null) {
			try {
				count1 = ((Double) Double.parseDouble(obj.toString()))
						.longValue();
			} catch (Exception e) {
				count1 = ((Double) obj).longValue();
			}
		}
		Long count2  = 0l;
		Object obj2 = o.mDetail.get(this.sortKey);
		if (obj2 != null) {
			try {
				count2 = ((Double) Double.parseDouble(obj2.toString()))
						.longValue();
			} catch (Exception e) {
				count2 = ((Double) obj2).longValue();
			}
		}
		if(count1 > count2){
			return -1;
		}else if(count1 < count2){
			return 1;
		}else{
			return 0;
		}
	}


	public String getKey() {
		return key;
	}
}
