import slick.jdbc.PostgresProfile.api._


import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Employee(id: Int, name: String, expr: Double)

trait EmployeeTable extends DbData{
  this:DbData=>
  import driver.api._

  val employeeTableQuery = TableQuery[EmployeeTable]

  class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {
    val id = column[Int]("id", O.PrimaryKey)
    val name = column[String]("name")
    val experience = column[Double]("expr")

    def * = (id, name, experience) <>(Employee.tupled, Employee.unapply)
  }

}

trait EmployeeRepo extends EmployeeTable {
  this: DbData =>

  import driver.api._

  //val db=this.config
  def create: Future[Unit] = db.run(employeeTableQuery.schema.create)

  def insert(emp: Employee): Future[Int] = db.run {
    employeeTableQuery += emp
  }

  def delete(emp: Double): Future[Int] = {
    val query = employeeTableQuery.filter(x => x.experience === emp)
    val action = query.delete
    db.run(action)
  }

  def updateName(id: Int, name: String): Future[Int] = {
    val query = employeeTableQuery.filter(_.id === id).map(_.name).update(name)
    db.run(query)
  }
  def find(id: Int) =
    db.run((for (emp <- employeeTableQuery if emp.id === id) yield emp).result.headOption)
  def upsert(emp: Employee): String ={
    val search=find(emp.id)
    search.map(x=> x match {
      case Some(i) => updateName(i.id, i.name)
      case _ => insert(emp)
    }
    )
    "success"
  }


  def CombineEmpAndProj:Future[Seq[(String, String)]]= {

    val combine = for {
      (e, p) <- employeeTableQuery join ProjectRepo.projectTableQuery on (_.id === _.eid)
    } yield (e.name, p.pname)
    val output: Future[Seq[(String, String)]] = db.run(combine.result)
    output
  }

  def unionByExperience: Future[Seq[Employee]] = {

    val q1 = employeeTableQuery.filter(_.experience < 8.0)
    val q2 = employeeTableQuery.filter(_.experience > 9.0)
    val unionQuery = q1 union q2
     db.run(unionQuery.result)
  }
  def minExperience: Future[Option[Double]]={
    val exp = employeeTableQuery.map(_.experience)
    val minexp = exp.min
    db.run(minexp.result)

  }
  def maxExperience: Future[Option[Double]]={
    val exp = employeeTableQuery.map(_.experience)
    val maxexp = exp.max
    db.run(maxexp.result)

  }

  def plainSql:Future[Vector[(Int,String,Double)]] = {
    val action = sql"select id, name, age from employee".as[(Int,String,Double)]


    db.run(action)
  }
  def search(id:Int): Future[List[Employee]] ={

    val action = employeeTableQuery.filter(_.id===id)
    db.run(action.to[List].result)
  }

  def andThen (emp1:Employee ,  emp2:Employee ):Future[Int]={

    val action1 = employeeTableQuery+=emp1
    val action2 = employeeTableQuery+=emp2
    val temp = action1.andThen(action2)
     db.run(temp)
 }
}



object EmployeeRepo extends EmployeeRepo with SqlDb