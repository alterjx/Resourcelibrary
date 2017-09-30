package com.resource.app.api.model;

import com.resource.app.model.PayProductMode;
import com.resource.app.model.UpgradeModel;

import java.util.LinkedHashMap;
import java.util.List;

public class PayConfigInfoOutput {
	public LinkedHashMap<Integer, String> payTypeMap;
	public List<PayProductMode> productInfos;
	public String payDeclare;
	public String payQuestion; //支付常见问题
}
