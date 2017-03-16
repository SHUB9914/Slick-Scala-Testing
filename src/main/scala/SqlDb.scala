

trait SqlDb extends DbData{

 override val driver=slick.jdbc.MySQLProfile
 import driver.api._
 override val db: Database = Database.forConfig("mysql1")

}
