package br.com.zup

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun freteClientStub(@GrpcChannel("fretes") channel: ManagedChannel)
            : CalculaFreteServiceGrpc.CalculaFreteServiceBlockingStub? {

        return CalculaFreteServiceGrpc.newBlockingStub(channel)
    }
}