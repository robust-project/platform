//package ie.deri.uimr.crosscom.cluster.feature.health;
//
//import ie.deri.uimr.crosscomanalysis.cluster.feature.health.*;
//import org.junit.Test;
//
//import java.util.Date;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by IntelliJ IDEA.
// * Author: vaclav.belak@deri.org
// * Date: 23/09/2011
// * Time: 16:52
// * ©2011 Digital Enterprise Research Institute, NUI Galway
// */
//public class TiddlyWikiHealthIndicatorsTest {
//
//    private final double PRECISION = 0.000001;
//
//    @Test
//    public void testAvgClustCoeff() {
//        AvgClustCoeffService s = new AvgClustCoeffService();
//        assertEquals(0, s.getHealthScore(0, new Date(105, 5, 15), new Date(106, 5, 15)), PRECISION);
//        assertEquals(0.0909865782340279, s.getHealthScore(3, new Date(105, 5, 15), new Date(106, 5, 15)), PRECISION);
//    }
//
//    @Test
//    public void testRelativeDensity() {
//        RelativeEdgeDensityService s = new RelativeEdgeDensityService();
//        assertEquals(0.333333333333333, s.getHealthScore(0, new Date(105, 5, 15), new Date(106, 5, 15)), PRECISION);
//        assertEquals(0.75, s.getHealthScore(16, new Date(110, 5, 15), new Date(111, 5, 15)), PRECISION);
//    }
//
//    @Test
//    public void testGroupBetweenness() {
//        GroupBetweennessService s = new GroupBetweennessService();
//        assertEquals(0, s.getHealthScore(0, new Date(105, 5, 15), new Date(106, 5, 15)), PRECISION);
//        assertEquals(0.591760962446016, s.getHealthScore(3, new Date(110, 5, 15), new Date(111, 5, 15)), PRECISION);
//    }
//
//    @Test
//    public void testSizeChange() {
//        SizeChangeService s = new SizeChangeService();
//        assertEquals(0, s.getHealthScore(0, new Date(105, 5, 15), new Date(106, 5, 15)), PRECISION);
//        assertEquals(-0.99986455170076, s.getHealthScore(3, new Date(110, 5, 15), new Date(111, 5, 15)), PRECISION);
//    }
//
//    @Test
//    public void testSize() {
//        SizeService s = new SizeService();
//        assertEquals(0.00332225913621262, s.getHealthScore(0, new Date(105, 5, 15), new Date(106, 5, 15)), PRECISION);
//        assertEquals(0.0140350877192982, s.getHealthScore(3, new Date(110, 5, 15), new Date(111, 5, 15)), PRECISION);
//    }
//
//    @Test
//    public void testAge() {
//        AgeService a = new AgeService();
//        assertEquals(6 / 6d, a.getHealthScore(3, new Date(110, 5, 15), new Date(111, 5, 15)), PRECISION);
//        assertEquals(1 / 6d, a.getHealthScore(0, new Date(105, 5, 15), new Date(106, 5, 15)), PRECISION);
//        assertEquals(0d, a.getHealthScore(0, new Date(104, 5, 15), new Date(105, 5, 15)), PRECISION);
//    }
//
//}
