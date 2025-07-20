package Test;

import busticket.DAO.AdminLocationsDAO;
import busticket.controller.AdminLocationsServlet;
import busticket.model.AdminLocations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Pham Van Hoai - CE181744
 */
@PrepareForTest(AdminLocationsDAO.class)
public class AdminLocationTest {

    @InjectMocks
    private AdminLocationsServlet adminLocationsServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    @Mock
    private AdminLocationsDAO adminLocationsDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock session and request dispatcher
        when(request.getSession()).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        // Mock constructor of AdminLocationsDAO
        PowerMockito.whenNew(AdminLocationsDAO.class).withNoArguments().thenReturn(adminLocationsDAO);

        // Mock methods to avoid real database calls
        doNothing().when(adminLocationsDAO).insertLocation(any(AdminLocations.class));
        doNothing().when(adminLocationsDAO).updateLocation(any(AdminLocations.class));
        doNothing().when(adminLocationsDAO).deleteLocation(anyInt()); // Mock delete to avoid real DB call
    }

    @Test
    void testAddLocation_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("locationName")).thenReturn("Hanoi Station");
        when(request.getParameter("address")).thenReturn("123 Hanoi St");
        when(request.getParameter("latitude")).thenReturn("21.0278");
        when(request.getParameter("longitude")).thenReturn("105.8342");
        when(request.getParameter("locationType")).thenReturn("Station");
        when(request.getParameter("description")).thenReturn("Main station");
        when(request.getParameter("status")).thenReturn("Active");

        adminLocationsServlet.doPost(request, response);

        verify(session).setAttribute(eq("success"), eq("Location added successfully!"));
        verify(response).sendRedirect(contains("/admin/locations"));
    }

    @Test
    void testAddLocation_MissingFields() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("locationName")).thenReturn("");
        when(request.getParameter("address")).thenReturn("123 Hanoi St");
        when(request.getParameter("latitude")).thenReturn("21.0278");
        when(request.getParameter("longitude")).thenReturn("105.8342");
        when(request.getParameter("locationType")).thenReturn("Station");
        when(request.getParameter("description")).thenReturn("Main station");
        when(request.getParameter("status")).thenReturn("Active");

        adminLocationsServlet.doPost(request, response);

        verify(session).setAttribute(eq("error"), eq("Please fill in all required fields."));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testEditLocation_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("locationId")).thenReturn("1");
        when(request.getParameter("locationName")).thenReturn("Hanoi Station Updated");
        when(request.getParameter("address")).thenReturn("123 Hanoi St Updated");
        when(request.getParameter("latitude")).thenReturn("21.0278");
        when(request.getParameter("longitude")).thenReturn("105.8342");
        when(request.getParameter("locationType")).thenReturn("Station");
        when(request.getParameter("description")).thenReturn("Updated main station");
        when(request.getParameter("status")).thenReturn("Active");

        adminLocationsServlet.doPost(request, response);

        verify(session).setAttribute(eq("success"), eq("Location updated successfully!"));
        verify(response).sendRedirect(contains("/admin/locations"));
    }

    @Test
    void testEditLocation_MissingLocationId() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("locationId")).thenReturn("");
        when(request.getParameter("locationName")).thenReturn("Hanoi Station Updated");
        when(request.getParameter("address")).thenReturn("123 Hanoi St Updated");
        when(request.getParameter("latitude")).thenReturn("21.0278");
        when(request.getParameter("longitude")).thenReturn("105.8342");
        when(request.getParameter("locationType")).thenReturn("Station");
        when(request.getParameter("description")).thenReturn("Updated main station");
        when(request.getParameter("status")).thenReturn("Active");

        adminLocationsServlet.doPost(request, response);

        verify(session).setAttribute(eq("error"), eq("Location ID is required."));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDeleteLocation_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("locationId")).thenReturn("20");
        when(request.getParameter("locationName")).thenReturn("Hanoi Station"); // Mock để vượt qua checkNull
        when(request.getParameter("address")).thenReturn("123 Hanoi St");
        when(request.getParameter("latitude")).thenReturn("21.0278");
        when(request.getParameter("longitude")).thenReturn("105.8342");
        when(request.getParameter("locationType")).thenReturn("Station");
        when(request.getParameter("description")).thenReturn("Main station");
        when(request.getParameter("status")).thenReturn("Active");

        adminLocationsServlet.doPost(request, response);

        verify(session).setAttribute(eq("success"), eq("Location deleted successfully!"));
        verify(response).sendRedirect(contains("/admin/locations"));
    }

    @Test
    void testDeleteLocation_MissingLocationId() throws Exception {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("locationId")).thenReturn("");
        when(request.getParameter("locationName")).thenReturn("Hanoi Station"); // Mock để vượt qua checkNull
        when(request.getParameter("address")).thenReturn("123 Hanoi St");
        when(request.getParameter("latitude")).thenReturn("21.0278");
        when(request.getParameter("longitude")).thenReturn("105.8342");
        when(request.getParameter("locationType")).thenReturn("Station");
        when(request.getParameter("description")).thenReturn("Main station");
        when(request.getParameter("status")).thenReturn("Active");

        adminLocationsServlet.doPost(request, response);

        verify(session).setAttribute(eq("error"), eq("Location ID is required."));
        verify(response).sendRedirect(contains("/admin/locations"));
    }

    @Test
    void testPostLocation_ExceptionHandling() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("locationName")).thenReturn("Hanoi Station");
        when(request.getParameter("address")).thenReturn("123 Hanoi St");
        when(request.getParameter("latitude")).thenReturn("invalid"); // Gây lỗi khi parseDouble
        when(request.getParameter("longitude")).thenReturn("105.8342");
        when(request.getParameter("locationType")).thenReturn("Station");
        when(request.getParameter("description")).thenReturn("Main station");
        when(request.getParameter("status")).thenReturn("Active");

        adminLocationsServlet.doPost(request, response);

        verify(session).setAttribute(eq("error"), contains("Error processing location"));
        verify(response).sendRedirect(contains("/admin/locations"));
    }
}
