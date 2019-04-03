/*
 * Copyright 2006-2014 linkhub.co.kr, Inc. or its affiliates. All Rights Reserved.
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import kr.co.linkhub.auth.LinkhubException;
import kr.co.linkhub.auth.Token;
import kr.co.linkhub.auth.TokenBuilder;

import com.google.gson.Gson;

/**
 * Implements of JusoLink Services.
 * 
 * @author KimSeongjun
 * @version 1.0.3
 */
public class AddressServiceImp implements AddressService {

	private static final String ServiceID = "JUSOLINK";
	private static final String DefaultServiceURL = "https://juso.linkhub.co.kr";
	private static final String APIVersion = "1.0";

	private String ServiceURL = DefaultServiceURL;
	
	private String AuthURL = null;
	
	private TokenBuilder tokenBuilder;

	private String linkID;
	private String secretKey;
	private Gson _gsonParser = new Gson();
	
	private static Token token = null;
	
	protected String getLinkID() {
		return linkID;
	}

	/**
	 * 링크아이디 설정. (링크허브에서 발급)
	 * 
	 * @param linkID
	 */
	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}

	protected String getSecretKey() {
		return secretKey;
	}

	/**
	 * 비밀키 설정 (Issued by Linkhub)
	 * 
	 * @param secretKey
	 *            SecretKey.
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	/**
	 * Proxy 인증 URL 설정.
	 * @param authURL
	 */
	public void setAuthURL(String authURL) {
		this.AuthURL = authURL;
	}
	
	/**
	 * Proxy 운영기 URL 설정.
	 * @param serviceURL
	 */
	public void setServiceURL(String serviceURL) {
		this.ServiceURL = serviceURL;
	}
	/* (non-Javadoc)
	 * @see com.jusolink.api.SearchService#getUnitCost()
	 */
	@Override
	public float getUnitCost() throws JusoLinkException {
		UnitCostResponse response = httpget("/Search/UnitCost", UnitCostResponse.class);

		return response.unitCost;
	}
	
	/* (non-Javadoc)
	 * @see com.jusolink.api.SearchService#getBalance()
	 */
	@Override
	public double getBalance() throws JusoLinkException {
		try {
			return getTokenbuilder().getPartnerBalance(
					this.getSessionToken(null));
		} catch (LinkhubException le) {
			throw new JusoLinkException(le);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.jusolink.api.SearchService#searchAddress
	 */
	@Override
	public SearchResult searchAddress(String IndexWord, Integer Page) throws JusoLinkException {
		return searchAddress(IndexWord, Page, null);
	}
	
	/* (non-Javadoc)
	 * @see com.jusolink.api.SearchService#searchAddress
	 */
	@Override
	public SearchResult searchAddress(String IndexWord, Integer Page, Integer PerPage) throws JusoLinkException {
		return searchAddress(IndexWord, Page, PerPage, null, null);
	}
	
	/* (non-Javadoc)
	 * @see com.jusolink.api.SearchService#searchAddress
	 */
	@Override
	public SearchResult searchAddress(String IndexWord, Integer Page, Integer PerPage , Boolean noSuggest, Boolean noDiff) throws JusoLinkException {
		if(Page != null && Page < 1) Page = null;
		if(PerPage != null) {
			if(PerPage < 0) PerPage = 20;
		}
		String url = "/Search";
		
		if(IndexWord == null || IndexWord.trim().isEmpty()) {
			throw new JusoLinkException(-20010003, "검색어가 입력되지 않았습니다.");
		}
		
		try {
			url += "?Searches=" + URLEncoder.encode(IndexWord, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new JusoLinkException(-99999999, "검색어 인코딩 오류");
		}
		
		if(Page != null) {
			url += "&PageNum=" + Page;
		}
		
		if(PerPage != null) {
			url += "&PerPage=" + PerPage;
		}
		
		if(noSuggest != null && noSuggest) {
			url += "&noSuggest=true";
		}
		if(noDiff != null && noDiff) {
			url += "&noDifferential=true";
		}
		
		return  httpget(url, SearchResult.class);
		
	}
	
	private TokenBuilder getTokenbuilder() {
		if (this.tokenBuilder == null) {
			tokenBuilder = TokenBuilder
					.newInstance(getLinkID(), getSecretKey())
					.ServiceID(ServiceID)
					.addScope("200");
			
			if(AuthURL != null) tokenBuilder.setServiceURL(AuthURL);
		}

		return tokenBuilder;
	}

	private String getSessionToken( String ForwardIP)
			throws JusoLinkException {
	
		boolean expired = true;

		if (token != null) {

			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			SimpleDateFormat subFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");
			subFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			try {
				Date expiration = format.parse(token.getExpiration());
				Date UTCTime = subFormat.parse(getTokenbuilder().getTime());
				expired = expiration.before(UTCTime);
				
			} catch (LinkhubException le){
				throw new JusoLinkException(le);
			} catch (ParseException e){
			}
		}

		if (expired) {
			try {
				token = getTokenbuilder().build(null, ForwardIP);
			} catch (LinkhubException le) {
				throw new JusoLinkException(le);
			}
		}

		return token.getSession_token();
	}

	/**
	 * Convert Object to Json String.
	 * 
	 * @param Graph
	 * @return jsonString
	 */
	protected String toJsonString(Object Graph) {
		return _gsonParser.toJson(Graph);
	}

	/**
	 * Convert JsonString to Object of Clazz
	 * 
	 * @param json
	 * @param clazz
	 * @return Object of Clazz
	 */
	protected <T> T fromJsonString(String json, Class<T> clazz) {
		return _gsonParser.fromJson(json, clazz);
	}

	/**
	 * 
	 * @param url
	 * @param CorpNum
	 * @param PostData
	 * @param UserID
	 * @param Action
	 * @param clazz
	 * @return returned object
	 * @throws JusoLinkException
	 */
	protected <T> T httppost(String url, String PostData, String Action, Class<T> clazz)
			throws JusoLinkException {
		HttpURLConnection httpURLConnection;
		try {
			URL uri = new URL(ServiceURL + url);
			httpURLConnection = (HttpURLConnection) uri.openConnection();
		} catch (Exception e) {
			throw new JusoLinkException(-99999999, "주소링크 API 서버 접속 실패", e);
		}

		httpURLConnection.setRequestProperty("Authorization", "Bearer " + getSessionToken(null));
		

		httpURLConnection.setRequestProperty("x-api-version".toLowerCase(),
				APIVersion);

		httpURLConnection.setRequestProperty("Accept-Encoding",	"gzip");

		if (Action != null && Action.isEmpty() == false) {
			httpURLConnection.setRequestProperty("X-HTTP-Method-Override",
					Action);
		}

		httpURLConnection.setRequestProperty("Content-Type",
				"application/json; charset=utf8");

		try {
			httpURLConnection.setRequestMethod("POST");
		} catch (ProtocolException e1) {
		}

		httpURLConnection.setUseCaches(false);
		httpURLConnection.setDoOutput(true);

		if ((PostData == null || PostData.isEmpty()) == false) {

			byte[] btPostData = PostData.getBytes(Charset.forName("UTF-8"));

			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(btPostData.length));

			DataOutputStream output = null;
		
			try {

				output = new DataOutputStream(
						httpURLConnection.getOutputStream());
				output.write(btPostData);
				output.flush();
				
			} catch (Exception e) {
				throw new JusoLinkException(-99999999,
						"Fail to POST data to Server.", e);
			} finally {
				if (output != null){
					try {
						output.close();
					} catch (IOException e) {
						throw new JusoLinkException(-99999999,
								"Jusolink func OutputStream close exception.", e);
					}
				}
			}

		}
		String Result = "";
		
		InputStream input = null;
		
		try {
			input = httpURLConnection.getInputStream();
			
			if (httpURLConnection.getContentEncoding().equals("gzip")) {
				Result = fromGzipStream(input);
			} else {
				Result = fromStream(input);
			}
			
		} catch (IOException e) {

			ErrorResponse error = null;
			InputStream is = null;
			
			try {
				is = httpURLConnection.getErrorStream();
				Result = fromStream(is);
				
				error = fromJsonString(Result, ErrorResponse.class);
			} catch (Exception E) {
				
			} finally {
				try {
					is.close();
				} catch (IOException e1) {
					throw new JusoLinkException(-99999999,
							"Jusolink func InputStream close exception.", e);
				}
			}

			if (error == null)
				throw new JusoLinkException(-99999999,
						"Fail to receive data from Server.", e);
			else
				throw new JusoLinkException(error.getCode(), error.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					throw new JusoLinkException(-99999999,
							"Jusolink func InputStream close exception.", e);
				}
			}
		}

		return fromJsonString(Result, clazz);
	}

	/**
	 * 
	 * @param url
	 * @param CorpNum
	 * @param UserID
	 * @param clazz
	 * @return returned object
	 * @throws JusoLinkException
	 */
	protected <T> T httpget(String url, Class<T> clazz) throws JusoLinkException {
		HttpURLConnection httpURLConnection;
		try {
			URL uri = new URL(ServiceURL + url);
			httpURLConnection = (HttpURLConnection) uri.openConnection();
		} catch (Exception e) {
			throw new JusoLinkException(-99999999, "주소링크 API 서버 접속 실패", e);
		}

		httpURLConnection.setRequestProperty("Authorization", "Bearer " + getSessionToken(null));
		
		httpURLConnection.setRequestProperty("Accept-Encoding",	"gzip");

		httpURLConnection.setRequestProperty("x-api-version".toLowerCase(),
				APIVersion);

		String Result = "";
		
		InputStream input = null;
		
		try {
			input = httpURLConnection.getInputStream();
			
			if (httpURLConnection.getContentEncoding().equals("gzip")) {
				Result = fromGzipStream(input);
			} else {
				Result = fromStream(input);
			}
			
		} catch (IOException e) {
			ErrorResponse error = null;
			InputStream is = null;
			try {
				is = httpURLConnection.getErrorStream();
				Result = fromStream(is);
				error = fromJsonString(Result, ErrorResponse.class);
			} catch (Exception E) {
			} finally {
				if (is != null){
					try {
						is.close();
					} catch (IOException e1) {
						throw new JusoLinkException(-99999999,
								"Jusolink func InputStream close exception.", e);
					}
				}
			}

			if (error == null)
				throw new JusoLinkException(-99999999,
						"Fail to receive data from Server.", e);
			else
				throw new JusoLinkException(error.getCode(), error.getMessage());
		} finally {
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					throw new JusoLinkException(-99999999,
							"Jusolink func InputStream close exception.", e);
				}
			}
		}

		return fromJsonString(Result, clazz);
	}
	
	private class ErrorResponse {

		private long code;
		private String message;

		public long getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

	}

	protected class UnitCostResponse {

		public float unitCost;

	}
	
	private static String fromStream(InputStream input) throws JusoLinkException {
		InputStreamReader is = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		
		try {
			is = new InputStreamReader(input,	Charset.forName("UTF-8"));
			sb = new StringBuilder();
			br = new BufferedReader(is);

			String read = br.readLine();

			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}
		} catch (IOException ignored){
			
		} finally {
			try {
				if (br != null) br.close();
				if (is != null) is.close();
			} catch (IOException e) {
				throw new JusoLinkException(-99999999,
						"Jusolink func fromStream stream close exception.", e);
			}
		}
		

		return sb.toString();
	}
	
	private static String fromGzipStream(InputStream input) throws JusoLinkException {
		GZIPInputStream zipReader = null;
		InputStreamReader is = null;		
		BufferedReader br = null;
		StringBuilder sb = null;
		
		try {
			zipReader = new GZIPInputStream(input);
			is = new InputStreamReader(zipReader, "UTF-8");
			br = new BufferedReader(is);
			sb = new StringBuilder();
	
			String read = br.readLine();
	
			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}
		} catch (IOException e) {
			throw new JusoLinkException(-99999999, 
					"Jusolink fromGzipStream func Exception", e);
		} finally {
			try {
				if (br != null) br.close();
				if (is != null) is.close();
				if (zipReader != null) zipReader.close();
			} catch (IOException e) {
				throw new JusoLinkException(-99999999,
					"Jusolink fromGzipStream func finally close Exception", e);
			}
		}
		
		return sb.toString();
	}
}
