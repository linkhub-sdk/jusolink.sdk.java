package com.jusolink.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SearchResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6098512346487869363L;
	
	private Long numFound;
	private Integer listSize;
	private Integer totalPage;
	private Integer page;
	private boolean chargeYN;
	private List<String> deletedWord;
	private String searches;
	private String suggest;
	private Map<String,Long> sidoCount;
	private List<Juso> juso;
	
	/**
	 * @return 검색 총계.
	 */
	public Long getNumFound() {
		return numFound;
	}
	/**
	 * @return 페이지 목록 개수.
	 */
	public Integer getListSize() {
		return listSize;
	}
	/**
	 * @return 총 페이지 수.
	 */
	public Integer getTotalPage() {
		return totalPage;
	}
	
	/**
	 * @return 검색된 현 페이지번호.
	 */
	public Integer getPage() {
		return page;
	}
	/**
	 * @return 과금여부.
	 */
	public boolean getChargeYN() {
		return chargeYN;
	}
	
	/**
	 * @return 검색 제외 단어 목록.
	 */
	public List<String> getDeletedWord() {
		return deletedWord;
	}
	/**
	 * @return 검색어
	 */
	public String getSearches() {
		return searches;
	}
	/**
	 * @return 제시어
	 */
	public String getSuggest() {
		return suggest;
	}
	/**
	 * @return 시도별 검색결과 수 목록
	 *  Key :
	 * 경기도 : GYEONGGI
	 * 경상북도 : GYEONGSANGBUK
	 * 경상남도 : GYEONGSANGNAM
	 * 서울특별시 : SEOUL
	 * 전라남도 : JEOLLANAM
	 * 충청남도 : CHUNGCHEONGNAM
	 * 전라북도 : JEOLLABUK
	 * 부산광역시 : BUSAN
	 * 강원도 : GANGWON
	 * 충청북도 : CHUNGCHEONGBUK
	 * 대구광역시 : DAEGU
	 * 인천광역시 : INCHEON
	 * 광주광역시 : GWANGJU
	 * 제주특별자치도 : JEJU
	 * 대전광역시 : DAEJEON
	 * 울산광역시 : ULSAN
	 * 세종특별자치시 : SEJONG
	 */
	public Map<String, Long> getSidoCount() {
		return sidoCount;
	}
	/**
	 * @return 검색 주소 목록.
	 */
	public List<Juso> getJuso() {
		return juso;
	}
}
