package Test;

import busticket.DAO.DriverRequestTripChangeDAO;
import busticket.controller.DriverTripChangeServlet;
import busticket.model.DriverRequestTripChange;
import busticket.model.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverTripCancelTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    HttpSession session;

    @Mock
    DriverRequestTripChangeDAO dao;

    @InjectMocks
    DriverTripChangeServlet servlet;

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUser_id(2);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    void testCancelSuccess() throws ServletException, IOException {
        lenient().when(request.getParameter("tripId")).thenReturn("10");
        lenient().when(request.getParameter("reason")).thenReturn("Personal reason");
        lenient().when(dao.getDriverIdFromUser(2)).thenReturn(1);
        lenient().when(dao.updateTripStatusToPending(10)).thenReturn(true);
        lenient().when(dao.createTripChangeRequest(any(DriverRequestTripChange.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(session).setAttribute("success", "Your request has been submitted successfully.");
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testCancelNoUser() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    void testCancelMissingTripId() throws ServletException, IOException {
        when(request.getParameter("tripId")).thenReturn(null);
        when(request.getParameter("reason")).thenReturn("Personal reason");

        servlet.doPost(request, response);

        verify(session).setAttribute("error", "Trip ID or reason cannot be empty.");
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testCancelMissingReason() throws ServletException, IOException {
        when(request.getParameter("tripId")).thenReturn("10");
        when(request.getParameter("reason")).thenReturn("");

        servlet.doPost(request, response);

        verify(session).setAttribute("error", "Trip ID or reason cannot be empty.");
        verify(response).sendRedirect(anyString());
    }

}
