package service.custom.impl;

import dto.OrderDTO;
import dto.OrderDetailDTO;
import entity.Order;
import entity.OrderDetail;
import org.modelmapper.ModelMapper;
import repository.RepositoryFactory;
import repository.custom.OrderDetailRepository;
import repository.custom.OrderRepository;
import repository.custom.ProductRepository;
import service.custom.OrderService;
import util.RepositoryType;

import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private static OrderServiceImpl orderService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    private OrderServiceImpl() {
        orderRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.ORDERS);
        orderDetailRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.ORDERPRODUCT);
        productRepository = RepositoryFactory.getInstance().getRepositoryType(RepositoryType.PRODUCT);
        modelMapper = new ModelMapper();
    }

    public static OrderServiceImpl getInstance() {
        return orderService == null ? orderService = new OrderServiceImpl() : orderService;
    }

    @Override
    public int getLastId() {
        try {
            return orderRepository.getLastOrderId();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<OrderDTO> getOrders() {
        try {
            List<OrderDetailDTO> orderDetailDTOList = new ArrayList<>();
            for (OrderDetail orderDetails : orderDetailRepository.getAll()) {
                orderDetailDTOList.add(modelMapper.map(orderDetails, OrderDetailDTO.class));
            }

            List<OrderDTO> orderDTOList = new ArrayList<>();
            for (Order order : orderRepository.getAll()) {
                OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

                List<OrderDetail> matchedDetails = new ArrayList<>();
                for (OrderDetailDTO orderDetail : orderDetailDTOList) {
                    if (orderDetail.getOrderId() == order.getId()) {
                        matchedDetails.add(modelMapper.map(orderDetail, OrderDetail.class));
                    }
                }
                order.setOrderDetailList(matchedDetails);
                orderDTOList.add(orderDTO);
            }
            return orderDTOList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderDTO getOrder(int id) {
        try {
            Order order = orderRepository.getById(String.valueOf(id));
            if (order != null) {
                return modelMapper.map(order, OrderDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addOrder(OrderDTO orderDTO) {
        try {
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (OrderDetailDTO orderDetailDTO : orderDTO.getOrderDetailList()) {
                orderDetails.add(modelMapper.map(orderDetailDTO, OrderDetail.class));
            }

            Order order = new Order(
                    orderDTO.getId(),
                    orderDTO.getOrderDate(),
                    orderDTO.getTotalPrice(),
                    orderDTO.getPaymentMethod(),
                    orderDTO.getUserId(),
                    orderDTO.getCustomerId(),
                    orderDetails
            );

            boolean isOrderSaved = orderRepository.create(order);
            if (isOrderSaved) {
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
