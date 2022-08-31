package com.SigmaDating.app.adapters

class demo {

    var name:String="RacRhiRttisghdh"
    fun main() {
        var hashMap=HashMap<Char,Int>()
     for(i in 0..name.length){
      if(hashMap.containsKey(name[i])){
          var t=hashMap.get(name[i])
          if (t != null) {
              hashMap.put(name[i],(t+1))
          }

      }else{

          hashMap.put(name[i],1)

      }


     }

       System.out.println(hashMap)

    }


}