import org.scalatest.AsyncFunSuite

/**
  * Created by knoldus on 15/3/17.
  */
class DependentTesting extends AsyncFunSuite {

  test("inserting the  data") {
    DependentRepo.insert(Dependent(2, "shubham1", "brother" , 20)).map(x=>assert(x==1))
  }


  test("delete the  data") {
    DependentRepo.delete(4).map(x=>assert(x==0))
  }
  test("update name"){

    DependentRepo.updateName(1,"karan").map(x=>assert(x==1))
  }

  test("test and then"){

    DependentRepo.andThen(Dependent(1, "shubham1", "brother" , 13),Dependent(1, "shubham1", "brother" , 20)).map(x=>assert(x==1))
 }
  test("min age"){

    DependentRepo.minAge.map(x=>assert(x.getOrElse(0)==20))
  }
  test("max age"){

    DependentRepo.maxAge.map(x=>assert(x.getOrElse(0)==20))
  }

  test("search by id"){

    DependentRepo.search(1).map(x=>assert(x.length==1))
  }
  test("test union "){

    DependentRepo.unionByAge.map(x=>assert(x.length==1))
  }

  test("join on employee table and dependent table on basis of id"){

    DependentRepo.CombineEmpAndProj.map(x=>assert(x.length==1))
  }

  test("select data from dependent using plain sql"){

    DependentRepo.plainSql.map(x=>assert(x== Vector((1,"karan",20)) ))
  }

  test("hendle primary key violation"){


    DependentRepo.insert(Dependent(2, "shubham1", "brother" , 20)).map(x=>assert(x==1))
  }







}
