package kr.co.nicevan.nvcat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import kr.co.nicevan.nvcat.service.PrinterService;
import kr.co.nicevan.nvcat.service.PrinterServiceImpl;
import kr.co.nicevan.nvcat.service.label.LabelService;
import kr.co.nicevan.nvcat.service.label.LabelServiceImpl;
import kr.co.nicevan.nvcat.service.login.LoginService;
import kr.co.nicevan.nvcat.service.login.LoginServiceImpl;
import kr.co.nicevan.nvcat.service.order.OrderService;
import kr.co.nicevan.nvcat.service.order.OrderServiceImpl;
import kr.co.nicevan.nvcat.service.receipt.ReceiptService;
import kr.co.nicevan.nvcat.service.receipt.ReceiptServiceImpl;
import kr.co.nicevan.nvcat.service.common.CommonService;
import kr.co.nicevan.nvcat.service.common.CommonServiceImpl;

public class AppConfig {

    private static AppConfig instance;

    private ReceiptService receiptService;
    private LabelService labelService;
    private CommonService commonService;
    private PrinterService printerService;
    private LoginService loginService;
    private OrderService orderService;

    private AppConfig() {
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public ReceiptService receiptService() {
        return (ReceiptService) getObject(receiptService, ReceiptServiceImpl.class);
    }

    public LabelService labelService() {
        return (LabelService) getObject(labelService, LabelServiceImpl.class);
    }

    public CommonService commonService() {
        return (CommonService) getObject(commonService, CommonServiceImpl.class);
    }

    public PrinterService printerService() {
        return (PrinterService) getObject(printerService, PrinterServiceImpl.class, new Object[]{commonService()}, new Class[]{CommonService.class});
    }

    public LoginService loginService() {
        return (LoginService) getObject(loginService, LoginServiceImpl.class);
    }

    public OrderService orderService() {
        return (OrderService) getObject(orderService, OrderServiceImpl.class);
    }

    public Object getObject(Object object, Class clazz) {
        try {
            if (object == null) {
                object = clazz.newInstance();
            }
            return object;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getObject(Object object, Class clazz, Object[] params, Class clazzes[]) {
        try {
            if (object == null) {
                Constructor con = clazz.getConstructor(clazzes);
                con.newInstance(params);
            }
            return object;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
