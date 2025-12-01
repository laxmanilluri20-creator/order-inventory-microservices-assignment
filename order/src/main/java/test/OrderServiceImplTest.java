package test;

import com.order.dto.OrderItem;
import com.order.dto.OrderRequest;
import com.order.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderServiceImplTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        orderService = new OrderServiceImpl(restTemplate);
    }


    @Test
    void placeOrder_callsInventoryForEachItem() {
        // Expect POST to inventory/update (one item)
        server.expect(requestTo("http://localhost:8081/inventory/update"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));

        OrderRequest req = new OrderRequest();
        req.setOrderId("ORD-1");
        req.setItems(List.of(new OrderItem("SKU-ABC", 3)));

        orderService.placeOrder(req);

        server.verify();
    }
}
