use musicdb;
select * from songs;

slect title from songs;

alter table songs add column filepath text after album;
alter table songs add column  duration VARCHAR(20) after filepath;


CREATE TABLE playlists (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE playlist_songs (
  playlist_id INT,
  song_id INT,
  FOREIGN KEY (playlist_id) REFERENCES playlists(id),
  FOREIGN KEY (song_id) REFERENCES songs(id)
);

update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\Enemy - Imagine dragons X J.I.D.mp3" where id = 1;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\Legends Never Die (ft. Against The Current) ｜ Worlds 2017 - League of Legends (1).mp3" where id = 2;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\After Dark - Mr.Kitty (Music Video - Lost In Translation) (1).mp3" where id = 3;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\My Ordinary Life-The Living Tombstone (1).mp3" where id = 4;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\LADY GAGA BLOOD MARY - GOJO VS SUKUNA (FAN ANIMATION MANGÁ JJK) (1).mp3" where id = 5;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\Soap&Skin - Me And The Devil (Lyrics) (1).mp3" where id = 6;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\Billie Eilish - No Time To Die (Official Music Video) (1).mp3" where id = 7;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\MGMT - Little Dark Age (Official Viedo) (1).mp3" where id = 8;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\Starboy (1).mp3" where id = 9;  
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\Set Fire to the Rain (1).mp3" where id = 10;
update songs set filepath = "C:\\Users\\Lenovo\\Music\\mp3\\Imagine Dragons - Believer (Official Music Video) (1).mp3" where id = 11;





