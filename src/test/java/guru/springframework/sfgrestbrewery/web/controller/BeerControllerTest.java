package guru.springframework.sfgrestbrewery.web.controller;

import guru.springframework.sfgrestbrewery.bootstrap.BeerLoader;
import guru.springframework.sfgrestbrewery.services.BeerService;
import guru.springframework.sfgrestbrewery.web.model.BeerDto;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebFluxTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    BeerService beerService;

    BeerDto beerDto;

    @BeforeEach
    void setUp() {
        beerDto = BeerDto.builder()
                .beerName("Test Beer")
                .beerStyle("PALE_ALE")
                .upc(BeerLoader.BEER_1_UPC).build();
    }

    @Test
    void getBeerById() {
        UUID uuid = UUID.randomUUID();
        given(beerService.getById(any(), any())).willReturn(Mono.just(beerDto));

        webTestClient.get()
                .uri("/api/v1/beer/" + uuid)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(beerDto.getBeerName()));

    }


    @Test
    void listBeers() {
        List<BeerDto> beers = new ArrayList<>();
        beers.add(beerDto);
        BeerPagedList beerPagedList = new BeerPagedList(beers);
        given(beerService.listBeers(any(), any(), any(), any())).willReturn(beerPagedList);

        webTestClient.get()
                .uri("/api/v1/beer/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerPagedList.class)
                .value(BeerPagedList::getContent, equalTo(beerPagedList.getContent()));
    }

    @Test
    void getBeerByUpc() {
        given(beerService.getByUpc(any())).willReturn(Mono.just(beerDto));

        webTestClient.get()
                .uri("/api/v1/beerUpc/" + beerDto.getUpc())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(beerDto.getBeerName()));
    }


}