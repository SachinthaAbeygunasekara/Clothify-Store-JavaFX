package service.custom.impl;

import dto.OrderDetailDTO;
import entity.OrderDetail;
import org.modelmapper.ModelMapper;
import repository.RepositoryFactory;
import repository.custom.OrderDetailRepository;
import service.custom.OrderDetailService;
import util.RepositoryType;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailServiceImpl implements OrderDetailService {

    private static OrderDetailServiceImpl orderDetailService;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;

    private OrderDetailServiceImpl() {
        orderDetailRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.ORDERPRODUCT);
        modelMapper = new ModelMapper();
    }

    public static OrderDetailServiceImpl getInstance() {
        return orderDetailService == null ? orderDetailService = new OrderDetailServiceImpl() : orderDetailService;
    }

    @Override
    public List<OrderDetailDTO> getOrderProducts() {
        List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
        try {
            List<OrderDetail> orderDetails = orderDetailRepository.getAll();
            for (OrderDetail orderDetail : orderDetails) {
                orderDetailDTOList.add(modelMapper.map(orderDetail, OrderDetailDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderDetailDTOList;
    }
}
