package com.jusolink.api;

import java.io.Serializable;
import java.util.List;

public class Juso implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4893051840332361380L;
	
	private String sectionNum;
	private String roadAddr1;
	private String roadAddr2;
	private String jibunAddr;
	private List<String> detailBuildingName;
	private String zipcode;
	private String dongCode;
	private String streetCode;
	private List<String> relatedJibun;
	
	/**
	 * @return 기초구역번호(AKA, 신우편번호.)
	 */
	public String getSectionNum() {
		return sectionNum;
	}
	/**
	 * @return 도로명 주소
	 */
	public String getRoadAddr1() {
		return roadAddr1;
	}
	
	/**
	 * @return 도로명 주소 부가정보
	 */
	public String getRoadAddr2() {
		return roadAddr2;
	}
	
	/**
	 * @return 지번주소
	 */
	public String getJibunAddr() {
		return jibunAddr;
	}
	/**
	 * @return 상세건물 목록.
	 */
	public List<String> getDetailBuildingName() {
		return detailBuildingName;
	}
	/**
	 * @return 구 우편번호
	 */
	public String getZipcode() {
		return zipcode;
	}
	/**
	 * @return 행정동 코드
	 */
	public String getDongCode() {
		return dongCode;
	}
	/**
	 * @return 도로 코드
	 */
	public String getStreetCode() {
		return streetCode;
	}
	/**
	 * @return 관련 지번 목록
	 */
	public List<String> getRelatedJibun() {
		return relatedJibun;
	}
}
