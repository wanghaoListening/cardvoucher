package com.cnleyao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.cnleyao.Message;
import com.cnleyao.dto.Shop;
import com.cnleyao.entity.CardVoucher;
import com.cnleyao.entity.UserVoucher;
import com.cnleyao.service.UserVoucherServiceI;
import com.cnleyao.service.VoucherManageServiceI;

@Controller
@RequestMapping("/phone")
public class PhoneController extends BaseController{

	@Autowired
	private VoucherManageServiceI vService;
	@Autowired
	private UserVoucherServiceI uService;


	/**
	 * 获取卡券模板(查看卡券模板就提示用户，你是否已经领取，是否有权限领取)
	 * */
	@RequestMapping("/getVoucherTemplate")
	public String getVoucherTemplate(String userId,Long voucherId,Model model){
		//TODO 此方法涉及到领取后显示code亦或是二维码
		CardVoucher voucher = vService.getVoucher(voucherId);
		//根据用户的id和模板id查看此用户是否已经获取了卡券
		UserVoucher userVoucher = uService.findUserVoucher(userId,voucherId);
		if(userVoucher!=null){
			model.addAttribute("userVoucher", userVoucher);
		}
		model.addAttribute("voucher", voucher);
		model.addAttribute("userId", userId);
		return "pages/manage/getVoucher_Phone";
	}
	/**
	 * 用户获取卡券的接口(创建卡券并返回卡券的code)
	 * */
	@RequestMapping("/acquireVoucher")
	public @ResponseBody
	long acquireVoucher(String userId,Long voucherId,Model model){
		
		return uService.acquireVoucher(userId,voucherId);
	
	}

	/**
	 * 微信端获取卡券的详细使用信息
	 * **/
	@RequestMapping("/getDiscDetail")
	public String getDiscDetail(Long voucherId,Model model){
		CardVoucher voucher = vService.getVoucher(voucherId);
		model.addAttribute("voucher", voucher);
		return "pages/manage/discDetails_phone";
	}

	/**
	 * 获取此卡券所适应的门店
	 * */
	@RequestMapping("/getAdaptedStore")
	public String getAdaptedStore(Long voucherId,Model model){
		//TODO 此卡券适应门店的功能暂时未完成
		return "pages/manage/adaptedStore_phone";
	}
	/**
	 * 用户进入进行核销(未领取卡券的用户依然允许它进入)
	 * */
	@RequestMapping("/enterVeric")
	public String vericVoucher(String userId,Model model){

		model.addAttribute("userId", userId);
		return "pages/manage/veStep_phone";
	}

	/**
	 * 根据卡券的code和userId查询卡券
	 * */
	@RequestMapping("/verificVoucher")
	public String verificVoucher(UserVoucher userVoucher,Model model){
		CardVoucher voucher = uService.getVoucherByCode(userVoucher);
		model.addAttribute("voucher", voucher);
		return "pages/manage/verific_phone";

	}

	/**
	 * 对卡券进行核销
	 * */

	@RequestMapping("/cancelVoucher")
	public @ResponseBody
	String cancelVoucher(String code){
		boolean b = uService.getStateByCode(code);
		//证明卡券之前已被核销过
		if(b){
			return Message.WARN_MSG;
		}else{
			uService.setVoucherVerific(code);
		}
		return Message.SUCCESS_MSG;
	}

	/**
	 * 用户查看自己的卡包
	 * */
	@RequestMapping("/seeVoucherBag")
	public String seeVoucherBag(String userId,Model model){
		List<CardVoucher> vouchers = uService.getVoucherByUid(userId);
		model.addAttribute("vouchers", vouchers);
		return "pages/manage/voucherBag_phone";
	}
	/**
	 * 
	 * */
	@RequestMapping("/getShopName")
	public @ResponseBody
	Shop getShopName(Long businessId){
		Shop store = null;
		String data = getHttpData("http://114.215.138.165/api/shops/"+businessId);
		if(data!=null && data.length()>0){
			store = JSON.parseObject(data,Shop.class);
		}
		return store;
	}
}
