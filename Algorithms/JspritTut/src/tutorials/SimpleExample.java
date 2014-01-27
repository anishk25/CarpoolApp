package tutorials;

import java.io.File;
import java.util.Collection;

import jsprit.analysis.toolbox.SolutionPrinter;
import jsprit.analysis.toolbox.SolutionPrinter.Print;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.io.VrpXMLWriter;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleImpl.Builder;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;

public class SimpleExample {
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
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 4
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
         * build services at the required locations, each with a capacity-demand of 1.
         */
        Service service1 = Service.Builder.newInstance("Passenger1", 1).setCoord(Coordinate.newInstance(5, 7)).build();
        Service service2 = Service.Builder.newInstance("Passenger2", 1).setCoord(Coordinate.newInstance(5, 13)).build();
        
        Service service3 = Service.Builder.newInstance("Passenger3", 1).setCoord(Coordinate.newInstance(15, 7)).build();
        Service service4 = Service.Builder.newInstance("Passenger4", 1).setCoord(Coordinate.newInstance(15, 13)).build();
        Service service5 = Service.Builder.newInstance("Passenger5", 1).setCoord(Coordinate.newInstance(1,1)).build();
        
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle1);
        vrpBuilder.addVehicle(vehicle2);
        vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4).addJob(service5);
        
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
        
        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");
        
        SolutionPrinter.print(bestSolution);
        
        /*
         * plot
         */
//        SolutionPlotter.plotSolutionAsPNG(problem, bestSolution, "output/solution.png", "solution");
        
      //  new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(200).display();
}
}
