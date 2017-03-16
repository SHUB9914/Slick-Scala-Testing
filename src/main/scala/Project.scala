import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

case class Project(pid: Int, pname: String, eid: Int)

trait ProjectTable extends EmployeeTable {

  val projectTableQuery = TableQuery[ProjectTable]

  class ProjectTable(tag: Tag) extends Table[Project](tag,"project") {

    val pid = column[Int]("pid")
    val pname = column[String]("pname")
    val eid = column[Int]("eid")

    def employeeProjectFK = foreignKey("emp_proj_fk", eid, employeeTableQuery)(_.id)

    def * = (pid, pname, eid) <> (Project.tupled, Project.unapply)
  }

}

trait ProjectRepo extends ProjectTable {
  this: DbData =>

  import driver.api._
  // val db=Database.forConfig("myPostgresDB")
  def create: Future[Unit] = db.run(projectTableQuery.schema.create)

  def insert(project: Project): Future[Int] = db.run {
    projectTableQuery += project
  }
  def getAll: Future[List[Project]] = {
    db.run { projectTableQuery.to[List].result}
  }
  def find(id: Int): Future[Option[Project]] =
    db.run((for (project <- projectTableQuery if project.pid === id) yield project).result.headOption)

  def upsert(project: Project): String ={
    val search=find(project.pid)
    search.map(x=> x match {
      case Some(i) => updateName(i.pid, i.pname)
      case _ => insert(project)
    }
    )
    "success"
  }
  def updateName(id: Int, name: String): Future[Int] = {
    val query = projectTableQuery.filter(_.pid === id).map(_.pname).update(name)
    db.run(query)
  }

  def delete(eid: Double): Future[Int] = {
    val query = projectTableQuery.filter(_.eid === eid.toInt)
    val action = query.delete
    db.run(action)
  }

  def CombineEmpAndProj:Future[Seq[(String, String)]]= {

    val combine = for {
      (e, p) <- employeeTableQuery join projectTableQuery on (_.id === _.eid)
    } yield (e.name, p.pname)
    val output: Future[Seq[(String, String)]] = db.run(combine.result)
    output
  }

  def andThen (pro1:Project ,  pro2:Project ):Future[Int]={

    val action1 = projectTableQuery+=pro1
    val action2 = projectTableQuery+=pro2
    val temp = action1.andThen(action2)
    db.run(temp)
  }

  def transactionallyAdd (pro1:Project ,  pro2:Project ):Future[Int]={

    val action1 = projectTableQuery+=pro1
    val action2 = projectTableQuery+=pro2
    val temp = action1.andThen(action2).transactionally
    db.run(temp)

  }

  def plainSql:Future[Vector[(Int,String,Int)]] = {
    val action = sql"select pid, pname, eid from project".as[(Int,String,Int)]


    db.run(action)
  }


}

object ProjectRepo extends ProjectRepo with SqlDb