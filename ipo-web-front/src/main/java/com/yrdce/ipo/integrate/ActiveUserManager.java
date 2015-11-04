package com.yrdce.ipo.integrate;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gnnt.MEBS.logonServerUtil.au.AUConnectManager;
import gnnt.MEBS.logonService.kernel.ILogonService;
import gnnt.MEBS.logonService.vo.CheckUserResultVO;
import gnnt.MEBS.logonService.vo.CheckUserVO;
import gnnt.MEBS.logonService.vo.RemoteLogonServerVO;

public class ActiveUserManager {
	private static final transient Log logger = LogFactory.getLog(ActiveUserManager.class);

	public static CheckUserResultVO checkUser(String userID, long sessionID, int fromModuleID, String selfLogonType, String fromLogonType,
			int selfModuleID) {
		CheckUserResultVO checkUserResultVO = new CheckUserResultVO();
		CheckUserVO checkUserVO = new CheckUserVO();
		checkUserVO.setUserID(userID);
		checkUserVO.setSessionID(sessionID);
		checkUserVO.setToModuleID(selfModuleID);
		checkUserVO.setLogonType(selfLogonType);
		checkUserResultVO = checkUser(checkUserVO, fromModuleID, fromLogonType);
		return checkUserResultVO;
	}

	/**
	 * 
	 * 用户跳转
	 * <br/>
	 * <br/>
	 * 
	 * @param checkUserVO
	 *            登录 RMI 信息
	 * @param fromModuleID
	 *            来源模块编号
	 * @param fromLogonType
	 *            来源登录类型 (web web服务登录、pc 电脑客户端登录、mobile 手机客户端登录)
	 * @return
	 */
	public static CheckUserResultVO checkUser(CheckUserVO checkUserVO, int fromModuleID, String fromLogonType) {
		CheckUserResultVO result = new CheckUserResultVO();
		result.setResult(-1);
		ILogonService logonService = null;

		RemoteLogonServerVO remoteLogonServerVO = null;
		if (checkUserVO.getToModuleID() > 0) {
			remoteLogonServerVO = AUConnectManager.getInstance().getRemoteLogonServerVO(223001);
		} else {
			remoteLogonServerVO = AUConnectManager.getInstance().getRemoteLogonServerVO();
		}
		try {
			logonService = remoteLogonServerVO.getRmiService();
			result = logonService.checkUser(checkUserVO, fromModuleID, fromLogonType);
		} catch (RemoteException e) {
			int times = remoteLogonServerVO.clearRMI();
			try {
				logonService = remoteLogonServerVO.getRmiService();
				result = logonService.checkUser(checkUserVO, fromModuleID, fromLogonType);
			} catch (RemoteException e1) {
				// if (clearRMITimes > 0 && times > clearRMITimes) {
				// LogonConfigPO logonConfigPO = logonManagerDAO.getLogonConfigByID(selfConfigID);
				// remoteLogonServerVO.setLogonConfigPO(logonConfigPO);
				// }
				result.setRecode("-1");
				result.setMessage("调用 AU 异常");
				logger.error("调用 AU 异常", e1);
			} catch (Exception e1) {
				result.setRecode("-1");
				result.setMessage("调用 AU 异常");
				logger.error("调用 AU 异常", e1);
			}
		} catch (Exception e) {
			result.setRecode("-1");
			result.setMessage("调用 AU 异常");
			logger.error("调用 AU 异常", e);
		}
		return result;
	}
}
