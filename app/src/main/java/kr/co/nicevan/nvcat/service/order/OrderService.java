package kr.co.nicevan.nvcat.service.order;

import kr.co.nicevan.nvcat.dto.KioskOrderDTO;

public interface OrderService {

    void saveKioskOrders(KioskOrderDTO.SaveOrders saveOrders);
}
