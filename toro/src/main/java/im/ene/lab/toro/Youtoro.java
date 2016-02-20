/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.toro;

/**
 * Created by eneim on 2/15/16.
 *
 * 設計方針
 *
 * 1. YoutubePlayer has different playback lifecycle with normal view.
 *
 * 2. Youtube Player API is SO ANNOYING
 *
 * 3. Fabric-style setup: Toro.init(MyApp.this, new Youtoro(YOUTUBE_KEY));
 */
public final class Youtoro {

  final String apiKey;

  public Youtoro(String apiKey) {
    this.apiKey = apiKey;
  }
}
