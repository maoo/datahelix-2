package com.scottlogic.deg.classifier

import org.apache.spark.sql.DataFrame

class DataFrameClassifier(df: DataFrame) {

  def getAnalysis(): Seq[ClassifiedField] = {
    df.schema.fields.map(field => {
      var typeList = df.rdd.flatMap(row => {
        val fieldValue = row.getAs[String](field.name)
        val fieldValueCleansed = if (fieldValue == null) "" else fieldValue
        MainClassifier.classify(fieldValueCleansed)
      }).groupBy(identity)
        .mapValues(_.size)
        .collectAsMap()

      if(typeList.keys.size == 1 && (typeList.contains(StringType) || typeList.contains(IntegerType))){
        val multiValueTypes = MainClassifier.classifyMany(df.rdd.map(row => row.getAs[String](field.name)))
        multiValueTypes.foreach(semanticType => {
          typeList = typeList + (semanticType -> Int.MaxValue)
        });
      }

      ClassifiedField(field.name, typeList)
    })
  }
}
