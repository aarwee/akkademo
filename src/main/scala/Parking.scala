import akka.actor.Actor
import akka.pattern.ask
import akka.actor.{Props, ActorSystem, Actor}
import akka.actor.Actor.Receive
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by knoldus on 18/3/16.
  * [user will send the ticket no for freeing the slot]
  */
object Parking  {

  val system = ActorSystem("Parking")
  val user = system.actorOf(Props[User],"user")
  val attendant = system.actorOf(Props[Attendant],"attendant")
  val monitor = system.actorOf(Props[Monitor],"monitor")

  def main(args: Array[String]) {

    user ! "locate"
    user ! "locate"
    user ! "locate"
    user ! "locate"
    user ! "2"
    user ! "locate"
    user ! "locate"
    user ! "5"
    user ! "locate"
    user ! "locate"
    user ! "locate"
    user ! "2"


  }

}


class User extends Actor{

  override def receive: Receive = {

    case "locate" => Parking.attendant ! "area locate "
    case loc => Parking.attendant ! loc

  }
}

class Attendant extends Actor {

  override def receive: Actor.Receive = {
    case "area locate " => {
      val result = (Parking.monitor ? "allocate space") (Timeout(5 seconds)).mapTo[Int]

       result.map{ output => println("seat allocated:" + output)}
    }
    case loc => {
      val result = (Parking.monitor ? loc) (Timeout(5 seconds)).mapTo[Int]
     result.map{output => println("slot freed : " + output)}

      // case loc => Parking.monitor ! loc
    }


  }
}



class Monitor extends  Actor{

  val area:Array[Boolean] = Array(false,false,false,false,false,false,false,false,false,false)

  def setArea():Int ={

    val ticket =  area.indexOf(false)
    if(ticket >=0)
    {
      area(ticket) = true

    }
    ticket

  }

  def freeArea(id:String):Int={

    val free = id.toInt
    area(free) = false
  //  println(iid+"free now")

  free

  }


  override def receive: Actor.Receive = {
    case "allocate space" => sender ! setArea()
    case loc => sender ! freeArea(loc.toString)

  }

}
