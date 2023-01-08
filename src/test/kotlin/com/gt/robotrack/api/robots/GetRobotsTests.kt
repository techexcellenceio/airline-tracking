package com.gt.robotrack.api.robots

import com.gt.robotrack.api.BaseApiTests
import com.gt.robotrack.robots.RobotDto
import com.gt.robotrack.utils.extractBodyAsRobot
import com.gt.robotrack.utils.robotIsCreated
import io.restassured.module.webtestclient.RestAssuredWebTestClient.get
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

class GetRobotsTests(@Autowired webTestClient: WebTestClient) : BaseApiTests(webTestClient) {

    @Test
    fun `Should be able to get  the robot after creating it`() {
        val tod = given().robotIsCreated(name = "TOD")

        val gottenRobot = get("robots/${tod.id}")
            .then()
            .status(HttpStatus.OK)
            .extractBodyAsRobot()

        assertThat(gottenRobot).isEqualTo(tod)
    }

    @Test
    fun `Should be able to get all the created robots`() {
        val robots = listOf("JOY", "R2D2")
            .map { name -> given().robotIsCreated(name) }

        val gottenRobots = get("robots")
            .then()
            .status(HttpStatus.OK)
            .extract()
            .body()
            .jsonPath()
            .getList(".", RobotDto::class.java)

        assertThat(gottenRobots).containsAll(robots)
    }

    @Test
    fun `Should fail when trying to get a reobot that does not exists`() {
        val id = (1000..100000).random()

        get("robots/$id")
            .then()
            .status(HttpStatus.NOT_FOUND)

    }

}