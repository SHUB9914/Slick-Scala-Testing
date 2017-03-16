import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._



case class Dependent(id: Int, name: String, relation: String, age: Int)

trait DependentTable extends EmployeeTable{
  val dependentTableQuery = TableQuery[DependentTable]

  class DependentTable(tag: Tag) extends Table[Dependent](tag, "dependent") {
    val id = column[Int]("emp_id")
    val name = column[String]("name")
    val relation = column[String]("relation")
    val age = column[Int]("age")
    def dependentForeignKey_FK = foreignKey("depd_emp_fk", id, employeeTableQuery)(_.id)
    def * = (id, name, relation, age) <>(Dependent.tupled, Dependent.unapply)
  }

}

trait DependentRepo extends DependentTable {
  this: DbData =>

  import driver.api._

  //val db=this.config
  def create: Future[Unit] = db.run(dependentTableQuery.schema.create)

  def insert(dependent: Dependent): Future[Int] = db.run {
    dependentTableQuery += dependent
  }

  def delete(id: Int): Future[Int] = {
    val query = dependentTableQuery.filter(x => x.id === id)
    val action = query.delete
    db.run(action)
  }

  def updateName(id: Int, name: String): Future[Int] = {
    val query = dependentTableQuery.filter(_.id === id).map(_.name).update(name)
    db.run(query)
  }


  def andThen (dep1:Dependent , dep2:Dependent ):Future[Int]={

    val action1 = dependentTableQuery+=dep1
    val action2 = dependentTableQuery+=dep2
    val temp = action1.andThen(action2)
    db.run(temp)
  }

  def minAge: Future[Option[Int]]={
    val exp = dependentTableQuery.map(_.age)
    val minexp = exp.min
    db.run(minexp.result)

  }


  def maxAge: Future[Option[Int]]={
    val exp = dependentTableQuery.map(_.age)
    val minexp = exp.max
    db.run(minexp.result)

  }

  def plainSql:Future[Vector[(Int,String,Int)]] = {
    val action = sql"select emp_id, name, age from dependent".as[(Int,String,Int)]


    db.run(action)
  }

  def search(id:Int): Future[List[Dependent]] ={

    val action = dependentTableQuery.filter(_.id===id)
    db.run(action.to[List].result)
  }

  def unionByAge: Future[Seq[Dependent]] = {

    val q1 = dependentTableQuery.filter(_.age < 30)
    val q2 = dependentTableQuery.filter(_.age > 50)
    val unionQuery = q1 union q2
    db.run(unionQuery.result)
  }

  def CombineEmpAndProj:Future[Seq[(String, String)]]= {

    val combine = for {
      (e, p) <- employeeTableQuery join dependentTableQuery on (_.id === _.id)
    } yield (e.name, p.name)
    val output: Future[Seq[(String, String)]] = db.run(combine.result)
    output
  }


}

object DependentRepo extends DependentRepo with H2DBComponent