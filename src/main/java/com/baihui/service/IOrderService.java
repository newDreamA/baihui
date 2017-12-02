package com.baihui.service;

import com.baihui.common.ServerResponse;

public interface IOrderService {
    ServerResponse createOrder(Integer userId);

}
