package br.com.zup

import com.google.protobuf.Any
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class CalculadoraDeFretesController(@Inject val grpcClient: CalculaFreteServiceGrpc.CalculaFreteServiceBlockingStub) {

    @Get("/api/fretes")
    fun calculaFrete(@QueryValue cep: String): FreteResponse {

        val request = CalculaFreteRequest.newBuilder()
            .setCep(cep)
            .build()

        try {
            val response = grpcClient.calculaFrete(request)
            return FreteResponse(cep = response.cep, valor = response.valor)

        } catch (e: StatusRuntimeException) {

            val statusCode = e.status.code
            val descriptionMessage = e.status.description

            if (statusCode == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, descriptionMessage)
            }

            if (statusCode == Status.Code.PERMISSION_DENIED) {
                val statusProto = StatusProto.fromThrowable(e)
                if (statusProto == null) {
                    throw HttpStatusException(HttpStatus.FORBIDDEN, descriptionMessage)
                }

                val anyDetails: Any = statusProto.detailsList.get(0)
                val errorDetails = anyDetails.unpack(ErrorDetails::class.java)

                throw HttpStatusException(HttpStatus.FORBIDDEN, "${errorDetails.code}: ${errorDetails.message}")
            }


            // e.message traz o status code e o description, pode ser perigoso como falha de segurança
            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

}