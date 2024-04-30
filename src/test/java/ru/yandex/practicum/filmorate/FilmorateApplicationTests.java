package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {
	@Autowired
	private WebTestClient webTestClient;

	private Long id = 0L;

	private final User user = User.builder()
			.id(++id)
			.login("dolore")
			.name("Nick Name")
			.email("mail@mail.ru")
			.birthday(LocalDate.of(1946, 8, 20))
			.build();

	private final User userFailLogin = User.builder()
			.id(++id)
			.login("dolore ullamco")
			.email("yandex@mail.ru")
			.birthday(LocalDate.of(2446, 8, 20))
			.build();

	private final User userFailEmail = User.builder()
			.id(++id)
			.login("dolore ullamco")
			.name("")
			.email("mail.ru")
			.birthday(LocalDate.of(1980, 8, 20))
			.build();

	private final User userFailBirthday = User.builder()
			.id(++id)
			.login("dolore")
			.name("")
			.email("test@mail.ru")
			.birthday(LocalDate.of(2446, 8, 20))
			.build();

	private final User userUpdate = User.builder()
			.id(1L)
			.login("doloreUpdate")
			.name("est adipisicing")
			.email("mail@yandex.ru")
			.birthday(LocalDate.of(1976, 9, 20))
			.build();

	private final User userUpdateFakeId = User.builder()
			.id(Long.MAX_VALUE)
			.login("doloreUpdate")
			.name("est adipisicing")
			.email("mail@yandex.ru")
			.birthday(LocalDate.of(1976, 9, 20))
			.build();

	private final Film film = Film.builder()
			.name("nisi eiusmod")
			.description("adipisicing")
			.releaseDate(LocalDate.of(1967, 3, 25))
			.duration(Duration.ofMinutes(100))
			.build();

	private final Film filmFailName = Film.builder()
			.id(++id)
			.name("")
			.description("Description")
			.releaseDate(LocalDate.of(1900, 3, 25))
			.duration(Duration.ofMinutes(200))
			.build();

	private final Film filmFailDescription = Film.builder()
			.id(++id)
			.name("Film name")
			.description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят "
					+ "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. "
					+ "о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
			.releaseDate(LocalDate.of(1900, 3, 25))
			.duration(Duration.ofMinutes(200))
			.build();

	private final Film filmFailReleaseDate = Film.builder()
			.id(++id)
			.name("Name")
			.description("Description")
			.releaseDate(LocalDate.of(1890, 3, 25))
			.duration(Duration.ofMinutes(200))
			.build();

	private final Film filmFailDuration = Film.builder()
			.id(++id)
			.name("Name")
			.description("Description")
			.releaseDate(LocalDate.of(1896, 3, 25))
			.duration(Duration.ofMinutes(-200))
			.build();

	private final Film filmUpdate = Film.builder()
			.id(1L)
			.name("Властелин колец: Две крепости")
			.description("Братство распалось, но Кольцо Всевластья должно быть уничтожено.")
			.releaseDate(LocalDate.of(2003, 8, 03))
			.duration(Duration.ofMinutes(179))
			.build();

	@Test
	void createAndUpdateUser() {
		webTestClient.post().uri("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(user), User.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(User.class);

		webTestClient.put().uri("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(userUpdate), User.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(User.class);
	}

	@Test
	void noCreateUserWithFailLogin() {
		webTestClient.post().uri("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(userFailLogin), User.class)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void noCreateUserWithFailEmail() {
		webTestClient.post().uri("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(userFailEmail), User.class)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void noCreateUserWithFailBirthday() {
		webTestClient.post().uri("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(userFailBirthday), User.class)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void noUpdateUserWithFakeId() {
		webTestClient.put().uri("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(userUpdateFakeId), User.class)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void getUsers() {
		webTestClient.get().uri("/users")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Map.class);
	}

	@Test
	void createAndUpdateFilm() {
		webTestClient.post().uri("/films")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(film), Film.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Film.class);

		webTestClient.put().uri("/films")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(filmUpdate), Film.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader()
				.contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Film.class);
	}

	@Test
	void noCreateFilmWithFailName() {
		webTestClient.post().uri("/films")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(filmFailName), Film.class)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void noCreateFilmWithFailDescription() {
		webTestClient.post().uri("/films")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(filmFailDescription), Film.class)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void noCreateFilmWithFailReleaseDate() {
		webTestClient.post().uri("/films")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(filmFailReleaseDate), Film.class)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void noCreateFilmWithFailDuration() {
		webTestClient.post().uri("/films")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(filmFailDuration), Film.class)
				.exchange()
				.expectStatus().isBadRequest();
	}
}
