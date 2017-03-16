import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by knoldus on 14/3/17.
  */
object TestDb {

  def main(args: Array[String]): Unit = {
//    EmployeeRepo.create
    val insertRes = EmployeeRepo.insert(Employee(21, "shubham1", 8.0D))
    Thread.sleep(1000)
    val res = insertRes.map { res => s"$res row inserted in employee table" }.recover {
      case ex: Throwable => ex.getMessage
    }
    res.map(println(_))
    Thread.sleep(1000)
    //ProjectRepo.create
    val insertproj=ProjectRepo.insert(Project(90,"staffing",21))
    Thread.sleep(1000)
    val resproj = insertproj.map { res => s"$res row inserted in project table" }.recover {
      case ex: Throwable => ex.getMessage
    }
    resproj.map(println(_))
    Thread.sleep(1000)
    val depend=DependentRepo.insert(Dependent(21,"shubhambro","brother",50))
    Thread.sleep(1000)
    val depnd= depend.map{res => s"$res row inserted in dependent"}.recover {
      case ex: Throwable => ex.getMessage
    }
    depend.map(println(_))
    Thread.sleep(1000)
    //ProjectRepo.create
    val alldata: Future[List[Project]] =ProjectRepo.getAll
    Thread.sleep(1000)
    alldata.map(x=>x.map(print _ ))

    val findindex: Future[Option[Project]] =ProjectRepo.find(21)
    Thread.sleep(1000)
    findindex.map(x=>println(x.toString))

      val joinResult = EmployeeRepo.CombineEmpAndProj
    Thread.sleep(1000)
      joinResult.map{x=>print(x.toList)}

    val max: Future[Option[Int]] = DependentRepo.maxAge
    Thread.sleep(1000)
    max.map(x=>println("Max=>"+x.getOrElse(0)))

    val serch: Future[List[Employee]] = EmployeeRepo.search(21)
    Thread.sleep(1000)
    serch.map(x=>println(x.toString()))

    val min: Future[Option[Double]] = EmployeeRepo.minExperience
    Thread.sleep(1000)
    min.map(x=>println("min=>"+x.getOrElse(0)))

    val plain: Future[Vector[(Int, String, Double)]] = EmployeeRepo.plainSql
    Thread.sleep(1000)
    plain.map(x=>println("plain sql result-----------"+x.toString()))





  }


}
