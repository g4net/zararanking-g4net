package com.inditex.zboost.repository;

import ch.qos.logback.core.joran.sanity.Pair;
import com.inditex.zboost.entity.Order;
import com.inditex.zboost.entity.OrderDetail;
import com.inditex.zboost.entity.ProductOrderItem;
import com.inditex.zboost.exception.NotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepositoryImpl extends BaseRepository<Order> implements OrderRepository {

    public OrderRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Order> getOrders(int limit) {

        /*
         * TODO: EXERCISE 2.a) Retrieve a list of the last N orders (remember to sort by
         * date)
         */

        String sql =
                """
                        SELECT * FROM ORDERS ORDER BY DATE
                        """
                ;

        return this.query(sql, Map.of(), Order.class).subList(0, limit);
    }

    @Override
    public List<Order> getOrdersBetweenDates(Date fromDate, Date toDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", new java.sql.Date(fromDate.getTime()));
        params.put("toDate", new java.sql.Date(toDate.getTime()));
        String sql = """
                SELECT id, date, status
                FROM Orders
                WHERE date BETWEEN :startDate AND :toDate
                """;

        return this.query(sql, params, Order.class);
    }

    @Override
    public OrderDetail getOrderDetail(long orderId) {

        /*
         * TODO: EXERCISE 2.b) Retrieve the details of an order given its ID
         *
         * Remember that, if an order is not found by its ID, you must notify it
         * properly as indicated in the contract
         * you are implementing (HTTP status code 404 Not Found). For this,
         * you can use the exception {@link
         * com.inditex.zboost.exception.NotFoundException}
         */

        HashMap<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);



        String sql =
                """
                        SELECT o.id, o.date, o.status, p.price * oi.quantity, sum(oi.quantity) FROM ORDERS o, ORDER_ITEMS oi, PRODUCTS p
                        WHERE oi.ORDER_ID = :orderId AND o.id == oi.order_id AND oi.product_id == p.id
                        """;

        return this.queryForObject(sql, params, OrderDetail.class);
    }
}
