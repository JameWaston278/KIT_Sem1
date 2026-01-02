public class RoundAndRound {
        public static void main(String[] args) {
                Circle circle1 = new Circle();
                circle1.radius = 5.1;
                // calculate the remaining attributes for circle1 here

                circle1.diameter = circle1.radius * 2;
                circle1.circumference = 2 * Math.PI * circle1.radius;
                circle1.area = Math.PI * Math.pow(circle1.radius, 2);

                Circle circle2 = new Circle();
                circle2.radius = 17.5;
                // calculate the remaining attributes for circle2 here

                circle2.diameter = circle2.radius * 2;
                circle2.circumference = 2 * Math.PI * circle2.radius;
                circle2.area = Math.PI * Math.pow(circle2.radius, 2);

                System.out.println("circle1.radius = " + circle1.radius);

                // add more output-commands here
                System.out.println("circle1.diameter = " + circle1.diameter);
                System.out.println(
                                "circle1.circumference = " + Math.round(circle1.circumference * 1000.0) / 1000.0);
                System.out.println("circle1.area = " + Math.round(circle1.area * 1000.0) / 1000.0);
                System.out.println("circle2.radius = " + circle2.radius);
                System.out.println("circle2.diameter = " + circle2.diameter);
                System.out.println(
                                "circle2.circumference = " + Math.round(circle2.circumference * 1000.0) / 1000.0);
                System.out.println("circle2.area = " + Math.round(circle2.area * 1000.0) / 1000.0);

                Cone cone1 = new Cone();
                cone1.base = circle1;
                cone1.height = 10.3;

                // calculate volume and surfaceArea of cone1
                cone1.volume = cone1.base.area * cone1.height / 3;
                cone1.surfaceArea = (cone1.base.circumference
                                * Math.sqrt(Math.pow(cone1.height, 2) * Math.pow(cone1.base.radius, 2))) / 2;

                Cone cone2 = new Cone();
                cone2.base = circle2;
                cone2.height = 10.3;

                // calculate volume and sufaceArea of cone2
                cone2.volume = cone2.base.area * cone2.height / 3;
                cone2.surfaceArea = (cone2.base.circumference
                                * Math.sqrt(Math.pow(cone2.height, 2) * Math.pow(cone2.base.radius, 2))) / 2;

                System.out.println("cone1.volume = " + Math.round(cone1.volume * 1000.0) / 1000.0);
                System.out.println("cone1.surfaceArea = " + Math.round(cone1.surfaceArea * 1000.0) / 1000.0);
                System.out.println("cone2.volume = " + Math.round(cone2.volume * 1000.0) / 1000.0);
                System.out.println("cone2.surfaceArea = " + Math.round(cone1.surfaceArea * 1000.0) / 1000.0);
        }
}
