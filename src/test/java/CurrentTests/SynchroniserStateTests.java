package CurrentTests;

import VPI.Entities.util.Utilities;
import VPI.PDClasses.PDService;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.VertecClasses.VertecService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SynchroniserStateTests {
    private SynchroniserState ss;
    private VertecService vertec;
    private PDService pipedrive;
    public static String PATH_TO_TESTFILES = "src/test/resources/faultToleranceFiles/";

    @Before
    public void setUp() throws IOException {

        MockitoAnnotations.initMocks(this);

        pipedrive = mock(PDService.class);
        vertec = mock(VertecService.class);


        when(pipedrive.getAllUsers()).thenReturn(SynchroniserTest.getDummyUsersResponse());
        when(vertec.getTeamDetails()).thenReturn(SynchroniserTest.getOldDummyTeamResponse()); // for initialisaton of importer

        when(vertec.getSalesTeam()).thenReturn(SynchroniserTest.getDummyVertecTeamResponse().getBody().getEmployees());

        this.ss = new SynchroniserState(vertec,pipedrive, PATH_TO_TESTFILES);
    }

//    @Test
//    public void canSetFileToCurrentTime() throws IOException {
//        String filepath = PATH_TO_TESTFILES + "setCurrentTimetest";
//        Utilities.overwriteSingleLineFile("Habba Babba", filepath );
//
//        String line = Utilities.loadSingleLineFileToString(filepath);
//
//        assertEquals("Habba Babba", line);
//
//        ss.setFileToCurrentTime(filepath);
//
//        //Test only passes, if the file contains a correctly formatted date-time
//        line = Utilities.loadSingleLineFileToString(filepath);
//        String[] dateTime =line.split("T");
//        String date = dateTime[0];
//        String time = dateTime[1];
//    }
//
//    @Test
//    public void canLoadCrashWindows() throws IOException {
//        ss.clearCrashWindows();
//        File source = new File(PATH_TO_TESTFILES + "canLoadCrashWindowsTestFile");
//        Utilities.overwriteFile(source, PATH_TO_TESTFILES + "crashWindows");
//        ss.loadCrashWindows();
//
//        Set<SynchroniserState.TimeInterval> intervals = ss.getCrashWindows();
//
//        SynchroniserState.TimeInterval ti1 = ss.new TimeInterval("2016-08-26T00:00:00", "2016-08-27T00:00:00");
//        SynchroniserState.TimeInterval ti2 = ss.new TimeInterval("2016-08-25T00:00:00", "2016-08-26T00:00:00");
//
//        assertEquals("Wrong number of intervals received", 2, intervals.size());
//
//        List<Boolean> t1Found = new ArrayList<>();
//        List<Boolean> t2Found = new ArrayList<>();
//
//        intervals.forEach(interval -> {
//            if (interval.equals(ti1)) {
//                t1Found.add(true);
//            } else if (interval.equals(ti2)) {
//                t2Found.add(true);
//            }
//        });
//
//        assertTrue(t1Found.contains(true));
//        assertTrue(t2Found.contains(true));
//        assertEquals("Crashwindow fields not set for object",2,  ss.getCrashWindows().size());
//    }
//
//    @Test
//    public void canClearTestWindows() throws IOException {
//        File source = new File(PATH_TO_TESTFILES + "canLoadCrashWindowsTestFile");
//        Utilities.overwriteFile(source, PATH_TO_TESTFILES + "crashWindows");
//
//        ss.clearCrashWindows();
//
//        String line = Utilities.loadSingleLineFileToString(PATH_TO_TESTFILES + "crashWindows");
//        assertNull(line);
//        assertTrue(ss.getCrashWindows().isEmpty());
//    }
//
//    @Test
//    public void canAddCrashWindowToExisting() throws IOException {
//        ss.clearCrashWindows();
//        File source = new File(PATH_TO_TESTFILES + "canLoadCrashWindowsTestFile");
//        Utilities.overwriteFile(source, PATH_TO_TESTFILES + "crashWindows");
//
//        String start = "2016-08-28T20:00:00";
//        String end = "2016-08-29T11:00:00";
//
//        Utilities.writeSingleLineFile( start, ss.PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime");
//        Utilities.writeSingleLineFile(end, ss.PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime");
//
//        ss.addCrashWindow();
//
//        ss.loadCrashWindows();
//
//        List<SynchroniserState.TimeInterval> crashWindows = new ArrayList<>();
//        crashWindows.addAll(ss.getCrashWindows());
//
//        assertEquals("Not all crashwindows loaded", 3, crashWindows.size());
//
//        System.out.println(ss.getCrashWindows());
//        assertEquals("Crashwindow not added", ss.new TimeInterval(start, end), crashWindows.get(0));
//
//        assertNull(Utilities.loadSingleLineFileToString(ss.PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime"));
//        assertNull(Utilities.loadSingleLineFileToString(ss.PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime"));
//
//    }
//
//    @Test
//    public void addCrashWindowBehavesCorrectlyForEmptyFiles() throws IOException {
//        Utilities.clearFile(ss.PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime");
//        Utilities.clearFile(ss.PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime");
//
//        ss.clearCrashWindows();
//        ss.addCrashWindow();
//        assertTrue("Internal crashWindows added", ss.getCrashWindows().isEmpty());
//
//        assertNull(Utilities.loadSingleLineFileToString(ss.PATH_TO_FAULT_TOLERANCE_FILES + "crashWindows"));
//
//        assertNull(Utilities.loadSingleLineFileToString(ss.PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime"));
//        assertNull(Utilities.loadSingleLineFileToString(ss.PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime"));
//    }
//
//    @Test
//    public void willCorrectlySayTimeIsWithinACrashWindow() {
//        String time = "2016-08-08T00:00:00";
//
//        SynchroniserState.TimeInterval i = ss.new TimeInterval("2016-08-05T00:00:00", "2016-08-12T00:00:00");
//        ss.getCrashWindows().add(i);
//
//        assertTrue(ss.modificationMadeByCrashingSync(time));
//    }
//
//    @Test
//    public void willCorrectlySayTimeIsntWithinACrashWindow() {
//        String time = "2036-08-08T00:00:00";
//
//        SynchroniserState.TimeInterval i = ss.new TimeInterval("2016-08-05T00:00:00", "2016-08-12T00:00:00");
//        ss.getCrashWindows().add(i);
//
//        assertFalse(ss.modificationMadeByCrashingSync(time));
//    }
//



}
