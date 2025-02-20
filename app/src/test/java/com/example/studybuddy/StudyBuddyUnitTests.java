package com.example.studybuddy;

import static org.junit.Assert.*;

import com.example.studybuddy.models.Group;
import com.example.studybuddy.models.Session;
import com.example.studybuddy.models.User;
import com.example.studybuddy.utils.DateTimeUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StudyBuddyUnitTests {
    private Group testGroup;
    private User testUser;
    private Session testSession;

    @Before
    public void setup() {
        // Initialize test user
        testUser = new User();
        testUser.setId("test123");
        testUser.setName("Test User");
        testUser.setEmail("test@usc.edu");

        // Initialize test group
        testGroup = new Group();
        testGroup.setId("group123");
        testGroup.setName("Test Group");
        testGroup.setCourseName("CSCI310");
        testGroup.setMembers(new ArrayList<>(Arrays.asList("test123")));

        // Initialize test session
        testSession = new Session();
        testSession.setId("session123");
        testSession.setTitle("Test Session");
        testSession.setGroupId("group123");
    }

    // Group Tests
    @Test
    public void testGroupCreation() {
        Group group = new Group("Study Group", "CSCI310");
        assertEquals("Study Group", group.getName());
        assertEquals("CSCI310", group.getCourseName());
        assertNotNull("Members list should be initialized", group.getMembers());
        assertTrue("Initial members list should be empty", group.getMembers().isEmpty());
    }

    @Test
    public void testGroupMembership() {
        String userId = "testUser123";
        testGroup.addMember(userId);
        assertTrue("User should be a member after adding", testGroup.isMember(userId));

        testGroup.removeMember(userId);
        assertFalse("User should not be a member after removal", testGroup.isMember(userId));
    }

    // Session Tests
    @Test
    public void testSessionCreation() {
        testSession.setTitle("New Study Session");
        testSession.setLocation("Library");
        assertEquals("New Study Session", testSession.getTitle());
        assertEquals("Library", testSession.getLocation());
    }

    @Test
    public void testSessionParticipants() {
        String userId = "participant123";
        testSession.addParticipant(userId);
        assertTrue("User should be a participant", testSession.isParticipant(userId));

        testSession.removeParticipant(userId);
        assertFalse("User should not be a participant after removal", testSession.isParticipant(userId));
    }

    // User Tests
    @Test
    public void testUserCreation() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@usc.edu");
        assertEquals("John Doe", user.getName());
        assertEquals("john@usc.edu", user.getEmail());
    }

    // Date and Time Tests
    @Test
    public void testTimeValidation() {
        assertTrue("Valid time should be accepted", DateTimeUtils.isValidTime("14:30"));
        assertTrue("Valid time should be accepted", DateTimeUtils.isValidTime("09:00"));
        assertFalse("Invalid time should be rejected", DateTimeUtils.isValidTime("25:00"));
    }

    @Test
    public void testDateValidation() {
        // Test only the format validation, not future date validation
        String futureDate = "25/12/2024";
        String invalidDate = "32/13/2024";

        assertTrue("Valid future date should be accepted", DateTimeUtils.isValidDate(futureDate));
        assertFalse("Invalid date should be rejected", DateTimeUtils.isValidDate(invalidDate));
    }

    // Additional Tests
    @Test
    public void testGroupMembers() {
        Group group = new Group();
        List<String> members = Arrays.asList("user1", "user2", "user3");
        group.setMembers(members);
        assertEquals("Group should have correct number of members", 3, group.getMembers().size());
    }

    @Test
    public void testSessionTiming() {
        Session session = new Session();
        session.setStartTime("09:00");
        session.setEndTime("10:00");
        assertNotEquals("Start and end time should be different",
                session.getStartTime(), session.getEndTime());
    }

    @Test
    public void testUserGroups() {
        User user = new User();
        String groupId = "group123";
        user.addGroup(groupId);
        assertTrue("User should be member of the group", user.getGroups().contains(groupId));
    }

    @Test
    public void testSessionValidation() {
        Session session = new Session();
        assertNotNull("Session should have initialized participants list",
                session.getParticipants());
        assertTrue("New session should have empty participants list",
                session.getParticipants().isEmpty());
    }

    @Test
    public void testGroupCreationTime() {
        Group group = new Group();
        assertNotNull("Group should have creation timestamp", group.getCreatedAt());
    }

    @Test
    public void testUserProfile() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@usc.edu");
        assertNotNull("User should have initialized courses list", user.getCourses());
        assertNotNull("User should have initialized groups list", user.getGroups());
    }
}