
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DbData {

  val driver:JdbcProfile;
  import driver.api._
  val db: Database;


}
