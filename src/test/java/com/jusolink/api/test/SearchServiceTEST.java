package com.jusolink.api.test;

import org.junit.Test;

import com.jusolink.api.JusoLinkException;
import com.jusolink.api.SearchResult;
import com.jusolink.api.AddressService;
import com.jusolink.api.AddressServiceImp;

public class SearchServiceTEST {

	private final String testLinkID = "TESTER_JUSO";
	private final String testSecretKey = "FjaRgAfVUPvSDHTrdd/uw/dt/Cdo3GgSFKyE1+NQ+bc=";

	private AddressService searchService;
	
	public SearchServiceTEST() {
		AddressServiceImp service = new AddressServiceImp();

		service.setLinkID(testLinkID);
		service.setSecretKey(testSecretKey);
		
		searchService = service;
	}
		
	@Test
	public void getBalance_TEST() throws JusoLinkException {

		double balance = searchService.getBalance();

		System.out.println(balance);
	}
	
	@Test
	public void getUnitCost_TEST() throws JusoLinkException {

		float unitCost = searchService.getUnitCost();

		System.out.println(unitCost);
	}
	
	@Test
	public void searchAddress_TEST() throws JusoLinkException {

		SearchResult searchResult = searchService.searchAddress("과주광역시 신창 호반", 1);

		System.out.println(searchResult.getNumFound());
	}

}
