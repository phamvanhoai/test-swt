package Test;

import busticket.DAO.AdminUsersDAO;
import busticket.controller.AdminUsersServlet;
import busticket.model.AdminDrivers;
import busticket.model.AdminUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@PrepareForTest(AdminUsersDAO.class)
public class AddCustomerTest {

    private AdminUsersServlet adminUsersServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private AdminUsersDAO adminUsersDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Instantiate the servlet without injecting DAO
        adminUsersServlet = new AdminUsersServlet();

        // Create mocks
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        adminUsersDAO = mock(AdminUsersDAO.class);

        // Mock constructor of AdminUsersDAO
        PowerMockito.whenNew(AdminUsersDAO.class).withNoArguments().thenReturn(adminUsersDAO);
    }

    @Test
    void testAddCustomer_PasswordMismatch() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van D");
        when(request.getParameter("email")).thenReturn("dana@gmail.com");
        when(request.getParameter("password")).thenReturn("abc123");
        when(request.getParameter("confirmPassword")).thenReturn("abc124");
        when(request.getParameter("role")).thenReturn("Customer");
        when(request.getParameter("status")).thenReturn("Active");

        when(request.getRequestDispatcher("/WEB-INF/admin/users/add-user.jsp")).thenReturn(dispatcher);

        adminUsersServlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAddCustomer_MissingFullName() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("email")).thenReturn("vana@gmail.com");
        when(request.getParameter("password")).thenReturn("Abcd1234@");
        when(request.getParameter("confirmPassword")).thenReturn("Abcd1234@");
        when(request.getParameter("role")).thenReturn("Customer");
        when(request.getParameter("status")).thenReturn("Active");

        when(request.getRequestDispatcher("/WEB-INF/admin/users/add-user.jsp")).thenReturn(dispatcher);

        adminUsersServlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAddCustomer_EmailAlreadyExists() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van C");
        when(request.getParameter("email")).thenReturn("admin@gmail.com");
        when(request.getParameter("password")).thenReturn("Abcd1234@");
        when(request.getParameter("confirmPassword")).thenReturn("Abcd1234@");
        when(request.getParameter("role")).thenReturn("Customer");
        when(request.getParameter("status")).thenReturn("Active");
        when(adminUsersDAO.isEmailExists("existing@gmail.com")).thenReturn(true);

        when(request.getRequestDispatcher("/WEB-INF/admin/users/add-user.jsp")).thenReturn(dispatcher);

        adminUsersServlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAddCustomer_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van D");
        when(request.getParameter("email")).thenReturn("dana@gmail.com");
        when(request.getParameter("password")).thenReturn("Abcd1234@");
        when(request.getParameter("confirmPassword")).thenReturn("Abcd1234@");
        when(request.getParameter("role")).thenReturn("Customer");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");
        when(adminUsersDAO.isEmailExists("dana@gmail.com")).thenReturn(false);
        when(adminUsersDAO.addUser(any(AdminUsers.class))).thenReturn(1);

        adminUsersServlet.doPost(request, response);

        verify(response).sendRedirect(contains("/admin/users?message=created"));
    }

    @Test
    void testAddCustomer_InvalidEmail() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van A");
        when(request.getParameter("email")).thenReturn("invalidEmail");
        when(request.getParameter("password")).thenReturn("Abcd1234@");
        when(request.getParameter("confirmPassword")).thenReturn("Abcd1234@");
        when(request.getParameter("role")).thenReturn("Customer");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");

        when(request.getRequestDispatcher("/WEB-INF/admin/users/add-user.jsp")).thenReturn(dispatcher);

        adminUsersServlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAddCustomer_InvalidPassword() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van A");
        when(request.getParameter("email")).thenReturn("nguyen@gmail.com");
        when(request.getParameter("password")).thenReturn("123");
        when(request.getParameter("confirmPassword")).thenReturn("123");
        when(request.getParameter("role")).thenReturn("Customer");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");

        when(request.getRequestDispatcher("/WEB-INF/admin/users/add-user.jsp")).thenReturn(dispatcher);

        adminUsersServlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAddCustomer_MissingRole() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van A");
        when(request.getParameter("email")).thenReturn("nguyen@gmail.com");
        when(request.getParameter("password")).thenReturn("Abcd1234@");
        when(request.getParameter("confirmPassword")).thenReturn("Abcd1234@");
        when(request.getParameter("role")).thenReturn("");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");

        when(request.getRequestDispatcher("/WEB-INF/admin/users/add-user.jsp")).thenReturn(dispatcher);

        adminUsersServlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testAddDriver_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van B");
        when(request.getParameter("email")).thenReturn("vanb@gmail.com");
        when(request.getParameter("password")).thenReturn("Abcd1234@");
        when(request.getParameter("confirmPassword")).thenReturn("Abcd1234@");
        when(request.getParameter("phone")).thenReturn("0911222333");
        when(request.getParameter("role")).thenReturn("Driver");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("licenseNumber")).thenReturn("123456789012");
        when(request.getParameter("licenseClass")).thenReturn("D");
        when(request.getParameter("hireDate")).thenReturn("2023-01-01");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");
        when(adminUsersDAO.isEmailExists("vanb@gmail.com")).thenReturn(false);
        when(adminUsersDAO.addUser(any(AdminUsers.class))).thenReturn(1);
        when(adminUsersDAO.addDriver(any(AdminDrivers.class))).thenReturn(1);

        adminUsersServlet.doPost(request, response);

        verify(response).sendRedirect(contains("/admin/users?message=driver_created"));
    }

    @Test
    void testAddDriver_InvalidLicenseNumber() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Nguyen Van B");
        when(request.getParameter("email")).thenReturn("vanb@gmail.com");
        when(request.getParameter("password")).thenReturn("Abcd1234@");
        when(request.getParameter("confirmPassword")).thenReturn("Abcd1234@");
        when(request.getParameter("phone")).thenReturn("0911222333");
        when(request.getParameter("role")).thenReturn("Driver");
        when(request.getParameter("status")).thenReturn("Active");
        when(request.getParameter("licenseNumber")).thenReturn("123"); // Invalid
        when(request.getParameter("licenseClass")).thenReturn("D");
        when(request.getParameter("hireDate")).thenReturn("2023-01-01");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");

        when(request.getRequestDispatcher("/WEB-INF/admin/users/add-user.jsp")).thenReturn(dispatcher);

        adminUsersServlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyList());
        verify(dispatcher).forward(request, response);
    }
}
