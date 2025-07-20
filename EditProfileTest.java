package Test;

import busticket.DAO.ProfileManagementDAO;
import busticket.controller.ProfileManagementServlet;
import busticket.model.Users;
import busticket.util.InputValidator;
import busticket.util.PasswordUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class EditProfileTest {

    @InjectMocks
    private ProfileManagementServlet profileManagementServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    @Mock
    private ProfileManagementDAO profileManagementDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock session và currentUser
        Users currentUser = new Users();
        currentUser.setUser_id(1);
        currentUser.setName("Pham Van Hoai");
        currentUser.setEmail("hoai@gmail.com");
        currentUser.setPassword(PasswordUtils.hashPassword("oldpass123"));
        currentUser.setPhone("0912345678");
        currentUser.setRole("Customer");
        currentUser.setStatus("Active");
        currentUser.setBirthdate(Timestamp.valueOf("1990-01-01 00:00:00"));
        currentUser.setGender("Male");
        currentUser.setAddress("123 Street");

        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getSession(false)).thenReturn(session);
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        // Mock request parameters
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("name")).thenReturn("Pham Van Hoai Updated");
        when(request.getParameter("email")).thenReturn("hoai.updated@gmail.com");
        when(request.getParameter("phone")).thenReturn("0912345678");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("456 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");

        // Mock DAO response
        when(profileManagementDAO.updateUser(any(Users.class))).thenReturn(true);

        // Execute
        profileManagementServlet.doPost(request, response);

        // Verify
        verify(response).sendRedirect(contains("/profile/view"));
        verify(session).setAttribute(eq("success"), eq("Profile updated successfully!"));
        verify(session, never()).setAttribute(eq("error"), anyString());
    }

    @Test
    void testUpdateProfile_InvalidName() throws Exception {
        // Mock request parameters
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("name")).thenReturn("A"); // Too short
        when(request.getParameter("email")).thenReturn("hoai@gmail.com");
        when(request.getParameter("phone")).thenReturn("0912345678");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");

        when(request.getRequestDispatcher("/WEB-INF/pages/profile-management/update-profile.jsp")).thenReturn(dispatcher);

        // Mock DAO to get existing profile
        Users profile = new Users();
        profile.setUser_id(1);
        profile.setName("Pham Van Hoai");
        profile.setEmail("hoai@gmail.com");
        profile.setPhone("0912345678");
        profile.setGender("Male");
        profile.setAddress("123 Street");
        profile.setBirthdate(Timestamp.valueOf("1990-01-01 00:00:00"));
        when(profileManagementDAO.getUserById(1)).thenReturn(profile);

        // Execute
        profileManagementServlet.doPost(request, response);

        // Verify
        verify(session).setAttribute(eq("error"), contains("Full Name must be between 3 and 20 characters"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testUpdateProfile_InvalidEmail() throws Exception {
        // Mock request parameters
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("name")).thenReturn("Pham Van Hoai");
        when(request.getParameter("email")).thenReturn("invalid-email");
        when(request.getParameter("phone")).thenReturn("0912345678");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("1990-01-01");

        when(request.getRequestDispatcher("/WEB-INF/pages/profile-management/update-profile.jsp")).thenReturn(dispatcher);

        // Mock DAO to get existing profile
        Users profile = new Users();
        profile.setUser_id(1);
        profile.setName("Pham Van Hoai");
        profile.setEmail("hoai@gmail.com");
        profile.setPhone("0912345678");
        profile.setGender("Male");
        profile.setAddress("123 Street");
        profile.setBirthdate(Timestamp.valueOf("1990-01-01 00:00:00"));
        when(profileManagementDAO.getUserById(1)).thenReturn(profile);

        // Execute
        profileManagementServlet.doPost(request, response);

        // Verify
        verify(session).setAttribute(eq("error"), contains("Email is required"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testUpdateProfile_InvalidBirthdate() throws Exception {
        // Mock request parameters
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("name")).thenReturn("Pham Van Hoai");
        when(request.getParameter("email")).thenReturn("hoai@gmail.com");
        when(request.getParameter("phone")).thenReturn("0912345678");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("address")).thenReturn("123 Street");
        when(request.getParameter("birthdate")).thenReturn("invalid-date");

        when(request.getRequestDispatcher("/WEB-INF/pages/profile-management/update-profile.jsp")).thenReturn(dispatcher);

        // Mock DAO to get existing profile
        Users profile = new Users();
        profile.setUser_id(1);
        profile.setName("Pham Van Hoai");
        profile.setEmail("hoai@gmail.com");
        profile.setPhone("0912345678");
        profile.setGender("Male");
        profile.setAddress("123 Street");
        profile.setBirthdate(Timestamp.valueOf("1990-01-01 00:00:00"));
        when(profileManagementDAO.getUserById(1)).thenReturn(profile);

        // Execute
        profileManagementServlet.doPost(request, response);

        // Verify
        verify(request).setAttribute(eq("error"), eq("Invalid birthdate format."));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testChangePassword_Mismatch() throws Exception {
        // Mock request parameters
        when(request.getParameter("action")).thenReturn("change-password");
        when(request.getParameter("oldPassword")).thenReturn("oldpass123");
        when(request.getParameter("newPassword")).thenReturn("newpass123");
        when(request.getParameter("confirmPassword")).thenReturn("wrongpass123");

        when(request.getRequestDispatcher("/WEB-INF/pages/profile-management/change-password.jsp")).thenReturn(dispatcher);

        // Execute
        profileManagementServlet.doPost(request, response);

        // Verify
        verify(session).setAttribute(eq("error"), eq("New password and confirmation do not match."));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testChangePassword_InvalidOldPassword() throws Exception {
        // Mock request parameters
        when(request.getParameter("action")).thenReturn("change-password");
        when(request.getParameter("oldPassword")).thenReturn("wrongpass");
        when(request.getParameter("newPassword")).thenReturn("newpass123");
        when(request.getParameter("confirmPassword")).thenReturn("newpass123");

        // Mock DAO responses
        when(profileManagementDAO.getHashedPassword(1)).thenReturn(PasswordUtils.hashPassword("oldpass123"));

        when(request.getRequestDispatcher("/WEB-INF/pages/profile-management/change-password.jsp")).thenReturn(dispatcher);

        // Execute
        profileManagementServlet.doPost(request, response);

        // Verify
        verify(session).setAttribute(eq("error"), eq("Old password is incorrect."));
        verify(dispatcher).forward(request, response);
    }
}
