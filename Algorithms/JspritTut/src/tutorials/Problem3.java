package tutorials;

import java.io.File;
import java.util.Collection;

import jsprit.analysis.toolbox.SolutionPrinter;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.Constraint;
import jsprit.core.problem.io.VrpXMLWriter;
import jsprit.core.problem.job.Delivery;
import jsprit.core.problem.job.Pickup;
import jsprit.core.problem.job.Shipment;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.problem.vehicle.VehicleImpl.Builder;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;

public class Problem3 {

	final static double earthRadius = 6371;
	
	public static void main(String[] args) {
		File dir = new File("output");
        // if the directory does not exist, create it
        if (!dir.exists()){
                System.out.println("creating directory ./output");
                boolean result = dir.mkdir();  
                if(result) System.out.println("./output created");  
        }
        
        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
         */
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType", 10);
        VehicleType vehicleType = vehicleTypeBuilder.build();
        
        /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
         */
        Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle1");
        
        vehicleBuilder.setLocationCoord(Coordinate.newInstance(convertLatToX(39.082989, -76.877349), convertLatToY(39.082989, -76.877349)));
        vehicleBuilder.setType(vehicleType);
        Vehicle vehicle1 = vehicleBuilder.build();
        
        vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle2");
        vehicleBuilder.setLocationCoord(Coordinate.newInstance(1, 13));
        vehicleBuilder.setType(vehicleType);
        Vehicle vehicle2 = vehicleBuilder.build();
        
      
        
        
        
        Pickup pickup1 = (Pickup) Pickup.Builder.newInstance("Dest1", 1).setCoord(Coordinate.newInstance(convertLatToX(
        		39.095796,-76.88482), convertLatToY(
        				39.095796,-76.88482))).build();
        Delivery delivery1 = (Delivery) Delivery.Builder.newInstance("39.084271,-76.873079", 1).setCoord(Coordinate.newInstance(convertLatToX(39.084271,-76.873079), convertLatToY(39.084271,-76.873079))).build();
                
        Pickup pickup2 = (Pickup) Pickup.Builder.newInstance("Dest2", 1).setCoord(Coordinate.newInstance(convertLatToX(
        		39.095796,-76.88482), convertLatToY(
        				39.095796,-76.88482))).build();
        Delivery delivery2 = (Delivery) Delivery.Builder.newInstance("39.089002,-76.875289", 1).setCoord(Coordinate.newInstance(convertLatToX(39.089002,-76.875289), convertLatToY(39.089002,-76.875289))).build();
        
        Pickup pickup3 = (Pickup) Pickup.Builder.newInstance("Dest3", 1).setCoord(Coordinate.newInstance(convertLatToX(
        		39.095796,-76.88482), convertLatToY(
        				39.095796,-76.88482))).build();
        Delivery delivery3 = (Delivery) Delivery.Builder.newInstance("39.089418,-76.884817", 1).setCoord(Coordinate.newInstance(convertLatToX(39.089418,-76.884817), convertLatToY(39.089418,-76.884817))).build();
        
        Pickup pickup4 = (Pickup) Pickup.Builder.newInstance("Dest4", 1).setCoord(Coordinate.newInstance(convertLatToX(
        		39.095796,-76.88482), convertLatToY(
        				39.095796,-76.88482))).build();
        Delivery delivery4 = (Delivery) Delivery.Builder.newInstance("39.102824,-76.871094", 1).setCoord(Coordinate.newInstance(convertLatToX(39.102824,-76.871094), convertLatToY(39.102824,-76.871094))).build();
        
        Pickup pickup5 = (Pickup) Pickup.Builder.newInstance("Dest5", 1).setCoord(Coordinate.newInstance(convertLatToX(
        		39.095796,-76.88482), convertLatToY(
        				39.095796,-76.88482))).build();
        Delivery delivery5 = (Delivery) Delivery.Builder.newInstance("39.093565,-76.864907", 1).setCoord(Coordinate.newInstance(convertLatToX(39.093565,-76.864907), convertLatToY(39.093565,-76.864907))).build();
        
       
        
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addProblemConstraint(Constraint.DELIVERIES_FIRST);
        vrpBuilder.addVehicle(vehicle1);
       // vrpBuilder.addVehicle(vehicle2);
        vrpBuilder.addJob(pickup1).addJob(pickup2).addJob(pickup3).addJob(pickup4).addJob(pickup5)
        .addJob(delivery1).addJob(delivery2).addJob(delivery3).addJob(delivery4).addJob(delivery5);
        
        VehicleRoutingProblem problem = vrpBuilder.build();
        
        /*
         * get the algorithm out-of-the-box. 
         */
        VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);
        
        /*
         * and search a solution
         */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        
        /*
         * get the best 
         */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
        
        /*
         * write out problem and solution to xml-file
         */
        new VrpXMLWriter(problem, solutions).write("output/shipment-problem3-with-solution.xml");
        
        /*
         * print nRoutes and totalCosts of bestSolution
         */
        SolutionPrinter.print(bestSolution);     

	}
	
	private static double convertLatToX(double lat,double lon){
		double x_cor = Math.cos(lat) * Math.cos(lon) * earthRadius;
		return x_cor;
	}
	private static double convertLatToY(double lat,double lon){
		double y_cor = Math.cos(lat) * Math.sin(lon) * earthRadius;
		return y_cor;
	}

}
