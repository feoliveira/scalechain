package io.scalechain.blockchain.api.command.rawtx

import io.scalechain.blockchain.{ErrorCode, ExceptionWithErrorCode}
import io.scalechain.blockchain.api.command.RpcCommand
import io.scalechain.blockchain.api.domain.{RpcError, RpcRequest, RpcResult}
import io.scalechain.blockchain.proto.Hash
import io.scalechain.blockchain.proto.HashFormat
import spray.json._
import spray.json.DefaultJsonProtocol._
/*
  CLI command :
    bitcoin-cli -testnet signrawtransaction 01000000011da9283b4ddf8d\
      89eb996988b89ead56cecdc44041ab38bf787f1206cd90b51e0000000000ffff\
      ffff01405dc600000000001976a9140dfc8bafc8419853b34d5e072ad37d1a51\
      59f58488ac00000000



  CLI output :
    {
        "hex" : "01000000011da9283b4ddf8d89eb996988b89ead56cecdc44041ab38bf787f1206cd90b51e000000006a47304402200ebea9f630f3ee35fa467ffc234592c79538ecd6eb1c9199eb23c4a16a0485a20220172ecaf6975902584987d295b8dddf8f46ec32ca19122510e22405ba52d1f13201210256d16d76a49e6c8e2edc1c265d600ec1a64a45153d45c29a2fd0228c24c3a524ffffffff01405dc600000000001976a9140dfc8bafc8419853b34d5e072ad37d1a5159f58488ac00000000",
        "complete" : true
    }

  Json-RPC request :
    {"jsonrpc": "1.0", "id":"curltest", "method": "signrawtransaction", "params": ["01000000011da9283b4ddf8d89eb996988b89ead56cecdc44041ab38bf787f1206cd90b51e0000000000ffffffff01405dc600000000001976a9140dfc8bafc8419853b34d5e072ad37d1a5159f58488ac00000000"] }

  Json-RPC response :
    {
      "result": << Same to CLI Output >> ,
      "error": null,
      "id": "curltest"
    }
*/

case class SignRawTransactionResult(
  // The resulting serialized transaction encoded as hex with any signatures made inserted.
  // If no signatures were made, this will be the same transaction provided in parameter #1
  hex      : String,// "01000000011da9283b4ddf8d89eb996988b89ead56cecdc44041ab38bf787f1206cd90b51e000000006a47304402200ebea9f630f3ee35fa467ffc234592c79538ecd6eb1c9199eb23c4a16a0485a20220172ecaf6975902584987d295b8dddf8f46ec32ca19122510e22405ba52d1f13201210256d16d76a49e6c8e2edc1c265d600ec1a64a45153d45c29a2fd0228c24c3a524ffffffff01405dc600000000001976a9140dfc8bafc8419853b34d5e072ad37d1a5159f58488ac00000000",

  // The value true if transaction is fully signed; the value false if more signatures are required
  complete : Boolean//true
) extends RpcResult


case class UnspentTranasctionOutput(
  // The TXID of the transaction the output appeared in. The TXID must be encoded in hex in RPC byte order
  txid      : Hash,
  // The index number of the output (vout) as it appeared in its transaction, with the first output being 0
  vout      : Int,
  // The output’s pubkey script encoded as hex
  scriptPubKey : String,
  // If the pubkey script was a script hash, this must be the corresponding redeem script
  redeemScript : Option[String]
)


/** SignRawTransaction: signs a transaction in the serialized transaction format
  * using private keys stored in the wallet or provided in the call.
  *
  * Parameter #1 : Transaction (String;hex, Required)
  *   The transaction to sign as a serialized transaction.
  *
  * Parameter #2 : Dependencies (Array, Optional)
  *   Unspent transaction output details. The previous outputs being spent by this transaction.
  *
  * Parameter #3 : Private Keys (Array, Optional)
  *   An array holding private keys.
  *   If any keys are provided, only they will be used to sign the transaction (even if the wallet has other matching keys).
  *   If this array is empty or not used, and wallet support is enabled, keys from the wallet will be used.
  *
  *   Array item : String;base58
  *     A private key in base58check format to use to create a signature for this transaction
  *
  * Parameter #4 : SigHash (String, Optional)
  *   The type of signature hash to use for all of the signatures performed.
  *   (You must use separate calls to the signrawtransaction RPC
  *    if you want to use different signature hash types for different signatures.
  *    The allowed values are: ALL, NONE, SINGLE, ALL|ANYONECANPAY, NONE|ANYONECANPAY, and SINGLE|ANYONECANPAY)
  *
  * Result: (Object)
  *   The results of the signature.
  *
  * https://bitcoin.org/en/developer-reference#signrawtransaction
  */
class InvalidRpcParameter extends Exception
object SignRawTransaction extends RpcCommand {

  val rpcErrorMap = Map(
    ErrorCode.RpcRequestParseFailure -> RpcError.RPC_INVALID_REQUEST,
    ErrorCode.RpcParameterTypeConversionFailure -> RpcError.RPC_INVALID_PARAMETER,
    ErrorCode.RpcMissingRequiredParameter -> RpcError.RPC_INVALID_REQUEST
  )

  def invoke(request : RpcRequest) : Either[RpcError, Option[RpcResult]] = {

    try {
      import HashFormat._
      implicit val implicitUnspentTranasctionOutput = jsonFormat4(UnspentTranasctionOutput.apply)

      // Convert request.params.paramValues, which List[JsValue] to SignRawTransactionParams instance.
      val transaction : String = request.params.get[String]("Transaction", 0)
      val dependencies : Option[List[UnspentTranasctionOutput]] = request.params.getListOption[UnspentTranasctionOutput]("Dependencies", 1)
      val privateKeys : Option[List[String]] = request.params.getListOption[String]("Private Keys", 2)
      val sigHash : Option[String] = request.params.getOption[String]("SigHash", 3)

      // TODO : Implement
      Right(
        Some(
          SignRawTransactionResult(
            "01000000011da9283b4ddf8d89eb996988b89ead56cecdc44041ab38bf787f1206cd90b51e000000006a47304402200ebea9f630f3ee35fa467ffc234592c79538ecd6eb1c9199eb23c4a16a0485a20220172ecaf6975902584987d295b8dddf8f46ec32ca19122510e22405ba52d1f13201210256d16d76a49e6c8e2edc1c265d600ec1a64a45153d45c29a2fd0228c24c3a524ffffffff01405dc600000000001976a9140dfc8bafc8419853b34d5e072ad37d1a5159f58488ac00000000",
            true
          )
        )
      )
    } catch {
      case e : ExceptionWithErrorCode => {
        val rpcError = rpcErrorMap(e.code)
        Left(RpcError( rpcError.code, rpcError.messagePrefix, e.message))
      }
    }
/*
      {

      case e : InvalidRpcParameter  => {
        Left(RpcError( RpcError.RPC_INVALID_PARAMS.code, RpcError.RPC_INVALID_PARAMS.messagePrefix, e.toString))
    }
*/
  }
  def help() : String =
    """signrawtransaction "hexstring" ( [{"txid":"id","vout":n,"scriptPubKey":"hex","redeemScript":"hex"},...] ["privatekey1",...] sighashtype )
      |
      |Sign inputs for raw transaction (serialized, hex-encoded).
      |The second optional argument (may be null) is an array of previous transaction outputs that
      |this transaction depends on but may not yet be in the block chain.
      |The third optional argument (may be null) is an array of base58-encoded private
      |keys that, if given, will be the only keys used to sign the transaction.
      |
      |
      |Arguments:
      |1. "hexstring"     (string, required) The transaction hex string
      |2. "prevtxs"       (string, optional) An json array of previous dependent transaction outputs
      |     [               (json array of json objects, or 'null' if none provided)
      |       {
      |         "txid":"id",             (string, required) The transaction id
      |         "vout":n,                  (numeric, required) The output number
      |         "scriptPubKey": "hex",   (string, required) script key
      |         "redeemScript": "hex"    (string, required for P2SH) redeem script
      |       }
      |       ,...
      |    ]
      |3. "privatekeys"     (string, optional) A json array of base58-encoded private keys for signing
      |    [                  (json array of strings, or 'null' if none provided)
      |      "privatekey"   (string) private key in base58-encoding
      |      ,...
      |    ]
      |4. "sighashtype"     (string, optional, default=ALL) The signature hash type. Must be one of
      |       "ALL"
      |       "NONE"
      |       "SINGLE"
      |       "ALL|ANYONECANPAY"
      |       "NONE|ANYONECANPAY"
      |       "SINGLE|ANYONECANPAY"
      |
      |Result:
      |{
      |  "hex" : "value",           (string) The hex-encoded raw transaction with signature(s)
      |  "complete" : true|false,   (boolean) If the transaction has a complete set of signatures
      |  "errors" : [                 (json array of objects) Script verification errors (if there are any)
      |    {
      |      "txid" : "hash",           (string) The hash of the referenced, previous transaction
      |      "vout" : n,                (numeric) The index of the output to spent and used as input
      |      "scriptSig" : "hex",       (string) The hex-encoded signature script
      |      "sequence" : n,            (numeric) Script sequence number
      |      "error" : "text"           (string) Verification or signing error related to the input
      |    }
      |    ,...
      |  ]
      |}
      |
      |Examples:
      |> bitcoin-cli signrawtransaction "myhex"
      |> curl --user myusername --data-binary '{"jsonrpc": "1.0", "id":"curltest", "method": "signrawtransaction", "params": ["myhex"] }' -H 'content-type: text/plain;' http://127.0.0.1:8332/
    """.stripMargin
}

