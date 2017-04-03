/**
 * 
 */
package aaTester

/**
 * @author GOXR3PLUS
 *
 */
public class GroovyTester {

    def i = -1

    public GroovyTester() {
	println"Hello from Groovy!!!"
	def list = 4 .. 1
	//list.sort(true)
	//list.forEach(
	//list.each{ println l }
	def list2 = ["Lars", "Ben", "Jack"]
	
	list2.each { print "Fuck yo mama $it \n"}
    }


    // O MY GOD IT'S GROOVY!!!!!!!!!!!!
    //The future language of XR3Player!!
     static def main(args) {
	new InternetConnectionTester()
    }
}
