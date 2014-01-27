package tutorials;

import java.io.File;
import java.util.Collection;


import jsprit.analysis.toolbox.SolutionPrinter;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.Constraint;
import jsprit.core.problem.io.VrpXMLWriter;
import jsprit.core.problem.job.Pickup;
import jsprit.core.problem.job.Shipment;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.problem.vehicle.VehicleImpl.Builder;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;


public class Problem2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
         * some preparation - create output folder
         */
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
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType", 3);
        VehicleType vehicleType = vehicleTypeBuilder.build();
        
        /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
         */
        Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle1");
        
        vehicleBuilder.setLocationCoord(Coordinate.newInstance(10, 10));
        vehicleBuilder.setType(vehicleType);
        Vehicle vehicle1 = vehicleBuilder.build();
        
        vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle2");
        vehicleBuilder.setLocationCoord(Coordinate.newInstance(1, 13));
        vehicleBuilder.setType(vehicleType);
        Vehicle vehicle2 = vehicleBuilder.build();
        
        /*
         * build shipments at the required locations, each with a capacity-demand of 1.
         * 4 shipments
         * 1: (5,7)->(6,9)
         * 2: (5,13)->(6,11)
         * 3: (15,7)->(14,9)
         * 4: (15,13)->(14,11)
         */
        
        Shipment shipment1 = Shipment.Builder.newInstance("Passenger1", 1).setPickupCoord(Coordinate.newInstance(1,1)).setDeliveryCoord(Coordinate.newInstance(5, 7)).build();
        Shipment shipment2 = Shipment.Builder.newInstance("Passenger2", 1).setPickupCoord(Coordinate.newInstance(1,1)).setDeliveryCoord(Coordinate.newInstance(5, 13)).build();
        
        Shipment shipment3 = Shipment.Builder.newInstance("Passenger3", 1).setPickupCoord(Coordinate.newInstance(1, 1)).setDeliveryCoord(Coordinate.newInstance(15, 7)).build();
        Shipment shipment4 = Shipment.Builder.newInstance("Passenger4", 1).setPickupCoord(Coordinate.newInstance(1, 1)).setDeliveryCoord(Coordinate.newInstance(15, 13)).build();
        Shipment shipment5 = Shipment.Builder.newInstance("Passenger5", 1).setPickupCoord(Coordinate.newInstance(1, 1)).setDeliveryCoord(Coordinate.newInstance(20, 4)).build();
        Shipment shipment6 = Shipment.Builder.newInstance("Passenger6", 1).setPickupCoord(Coordinate.newInstance(1, 1)).setDeliveryCoord(Coordinate.newInstance(8, 19)).build();
       
        
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addProblemConstraint(Constraint.DELIVERIES_FIRST);
        vrpBuilder.addVehicle(vehicle1);
        vrpBuilder.addVehicle(vehicle2);
        vrpBuilder.addJob(shipment1).addJob(shipment2).addJob(shipment3).addJob(shipment4).addJob(shipment5).addJob(shipment6);
        
        
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
        new VrpXMLWriter(problem, solutions).write("output/shipment-problem2-with-solution.xml");
       
        
        /*
         * print nRoutes and totalCosts of bestSolution
         */
        SolutionPrinter.print(bestSolution);
	}
}