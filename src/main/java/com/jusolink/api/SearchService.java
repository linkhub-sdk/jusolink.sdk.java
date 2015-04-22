/*
 * Copyright 2006-2014 innopost.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.jusolink.api;

/**
 * JusoLink SearchService Interface.
 * 
 * @author KimSeongjun
 * @version 1.0.0
 */
public interface SearchService {

	/**
	 * 잔여포인트 확인
	 * 
	 * @return RemainPoint
	 * @throws JusoLinkException
	 */
	public abstract double getBalance()
			throws JusoLinkException;

	
	/**
	 * 검색 단가 확인
	 * 
	 * @return 단가 (ex. 1.0)
	 * @throws PopbillException
	 */
	public float getUnitCost() throws JusoLinkException;
	
	/**
	 * 주소 검색
	 * 
	 * @param SearchWord : 검색어
	 * @param Page : 페이지번호
	 * @return
	 * @throws JusoLinkException
	 */
	public SearchResult searchAddress(String SearchWord , Integer Page) throws JusoLinkException;
	
	/**
	 * 주소 검색
	 * 
	 * @param SearchWord : 검색어
	 * @param Page : 페이지번호
	 * @param PerPage : 페이지당 검색수. 기본 20, 최대 50.
	 * @return
	 * @throws JusoLinkException
	 */
	public SearchResult searchAddress(String SearchWord , Integer Page, Integer PerPage) throws JusoLinkException;
	
	/**
	 * 주소 검색
	 * 
	 * @param SearchWord : 검색어
	 * @param Page : 페이지번호
	 * @param PerPage : 페이지당 검색수. 기본 20, 최대 50.
	 * @param noSuggest : 제시어 확인여부
	 * @param noDiff : 차등검색 시도여부
	 * @return
	 * @throws JusoLinkException
	 */
	public SearchResult searchAddress(String SearchWord , Integer Page, Integer PerPage , Boolean noSuggest, Boolean noDiff) throws JusoLinkException;

}