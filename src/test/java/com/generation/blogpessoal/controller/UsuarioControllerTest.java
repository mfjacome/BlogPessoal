package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

			@Autowired
			private TestRestTemplate testRestTemplate;
			
			@Autowired
			private UsuarioService usuarioService;
			
			@Autowired
			private UsuarioRepository usuarioRepository;
			
			@BeforeAll
			void start() {
				
				usuarioRepository.deleteAll();
				
				usuarioService.cadastrarUsuario(new Usuario(0L, 
						"Root", "root@root.com", "rootroot", " "));
			}
			
			@Test
			@DisplayName("Cadastrar um usuário")
			public void deveCriarUmUsuario( ) {
				
				HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Paulo Antines", "paulo_antunes@email.com.br", "123456",
						"https://i.imgur.com/JR7kUFU.jpg"));
				
				ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
				
				assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
				assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
				assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
			}
			
			@Test
			@DisplayName("Não permite duplicação de usuário")
			public void naoDeveDuplicarUsuario() {
				
				usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "123456", ""));
				
				HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario> (new Usuario(0L, 
						"Maria da Silva", "maria_silva@email.com.br", "123456", ""));
				
				ResponseEntity<Usuario> corpoReposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
				
				assertEquals(HttpStatus.BAD_REQUEST, corpoReposta.getStatusCode());
			}

			@Test
			@DisplayName("Atualizar um usuário")
			public void deveAtualizarUmUsuario() {
				
				Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews",
						"juliana_andrews@email.com.br", "123456", "https://i.imgur.com/JR7kUFU.jpg"));
				
				Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), 
						"Juliana Andrews Ramos", "juliana_ramos@email.com.br", "123456", "https://i.imgur.com/JR7kUFU.jpg");

				HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

				ResponseEntity<Usuario> corpoResposta = testRestTemplate
						.withBasicAuth("root@root.com", "rootroot")
						.exchange("/usuarios/cadastrar", HttpMethod.PUT,
						corpoRequisicao, Usuario.class);

				assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
				assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
				assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
			}
			
			@Test
			@DisplayName("Listar todos os usuários")
			public void deveMostrarTodosUsuarios() {
				
				usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "123456", ""));
				
				usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Gomes", "gomes_ricardo@email.com.br", "gomes123456", ""));
				
				ResponseEntity<String> resposta = testRestTemplate
						.withBasicAuth("root@root.com", "rootroot")
						.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
				
				assertEquals(HttpStatus.OK, resposta.getStatusCode());
			}
}

