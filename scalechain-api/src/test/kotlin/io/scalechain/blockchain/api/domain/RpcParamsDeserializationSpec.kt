package io.scalechain.blockchain.api.domain

import com.google.gson.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.Matchers
import io.kotlintest.specs.FlatSpec
import io.scalechain.blockchain.ErrorCode
import io.scalechain.blockchain.RpcException
import io.scalechain.blockchain.api.Json
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class RpcParamsDeserializationSpec : FlatSpec(), Matchers {

  override fun beforeEach() {
    // set-up code
    //

    super.beforeEach()
  }

  override fun afterEach() {
    super.afterEach()
    // tear-down code
    //
  }

  init {
    "RpcParams" should "be able to leave list of JsValues as is, if we want" {

      val jsonrpcValue = "1.0"
      val id = 1L
      val method = "mymethod"
      val arrayParam = JsonArray()
      arrayParam.add( JsonPrimitive("2") )
      arrayParam.add( JsonPrimitive(3.5) )

      val arrayParamInObject = JsonArray()
      arrayParamInObject.add(JsonPrimitive("4"))

      val objectParam = JsonObject()
      objectParam.add("field", JsonPrimitive("3"))
      objectParam.add("arr", arrayParamInObject)

      val objectArray = JsonArray()
      val obj1 = JsonObject()
      obj1.add("field", JsonPrimitive("5"))
      val obj1Arr = JsonArray()
      obj1Arr.add( JsonPrimitive("6"))
      obj1.add("arr", obj1Arr)
      val obj2 = JsonObject()
      obj2.add("field", JsonPrimitive(4.5))

      val params = JsonArray()
      // a simple string
      params.add(JsonPrimitive("1"))
      // a simple number
      params.add(JsonPrimitive(1.5))
      // a simple array
      params.add(arrayParam)
      // an object with an array field
      params.add(objectParam)
      // an array of objects, which may have an array of values
      params.add(objectArray)

      val jsObject = JsonObject()
      jsObject.add("jsonrpc", JsonPrimitive(jsonrpcValue) )
      jsObject.add("id", JsonPrimitive(id))
      jsObject.add("method", JsonPrimitive(method))
      jsObject.add("params", params )

      val request = Json.get().fromJson(jsObject, RpcRequest::class.java)

      request.jsonrpc shouldBe jsonrpcValue
      request.id shouldBe id
      request.method shouldBe method
      request.params.paramValues shouldBe params.toList()
    }

    "RpcParams" should "not throw a RpcException even though jsonrpc field is missing " {
      val jsObject = JsonObject()
      jsObject.add("id", JsonPrimitive(1))
      jsObject.add("method", JsonPrimitive("myMethod"))
      val params = JsonArray()
      params.add(JsonPrimitive("arg1"))
      jsObject.add("params", params)

      Json.get().fromJson(jsObject, RpcRequest::class.java)
    }

    "RpcParams" should "throw RpcException if id field is missing " {


      val jsObject = JsonObject()
      jsObject.add("jsonrpc", JsonPrimitive("1.0") )
      jsObject.add("method", JsonPrimitive("myMethod"))
      val params = JsonArray()
      params.add(JsonPrimitive("arg1"))
      jsObject.add("params", params)

      val thrown = shouldThrow <RpcException> {
        Json.get().fromJson(jsObject, RpcRequest::class.java)
      }
      thrown.code shouldBe ErrorCode.RpcRequestParseFailure
      thrown.message shouldBe "id field is missing."
    }

    "RpcParams" should "throw RpcException if method field is missing " {

      val jsObject = JsonObject()
      jsObject.add("jsonrpc", JsonPrimitive("1.0") )
      jsObject.add("id", JsonPrimitive(1))
      val params = JsonArray()
      params.add(JsonPrimitive("arg1"))
      jsObject.add("params", params)

      val thrown = shouldThrow <RpcException> {
        Json.get().fromJson(jsObject, RpcRequest::class.java)
      }
      thrown.code shouldBe ErrorCode.RpcRequestParseFailure
      thrown.message shouldBe "method field is missing."
    }

    "RpcParams" should "throw RpcException if params field is missing " {
      val jsObject = JsonObject()
      jsObject.add("jsonrpc", JsonPrimitive("1.0") )
      jsObject.add("id", JsonPrimitive(1))
      jsObject.add("method", JsonPrimitive("myMethod"))

      val thrown = shouldThrow <RpcException> {
        Json.get().fromJson(jsObject, RpcRequest::class.java)
      }
      thrown.code shouldBe ErrorCode.RpcRequestParseFailure
      thrown.message shouldBe "params field is missing."
    }

    "RpcParams" should "throw RpcException if params field is not an array " {
      val jsObject = JsonObject()
      jsObject.add("jsonrpc", JsonPrimitive("1.0") )
      jsObject.add("id", JsonPrimitive(1))
      jsObject.add("method", JsonPrimitive("myMethod"))
      jsObject.add("params", JsonPrimitive("arg1"))

      val thrown = shouldThrow <RpcException> {
        Json.get().fromJson(jsObject, RpcRequest::class.java)
      }
      thrown.code shouldBe ErrorCode.RpcRequestParseFailure
      thrown.message shouldBe "params field should be an array."
    }
  }
}
