'''
Created on 04.02.2016.

@author: Nikola Milekic
'''
from jinja2.environment import Environment
from jinja2.loaders import PackageLoader
import pydot
from textx.export import metamodel_export, model_export
from textx.metamodel import metamodel_from_file

from basket.entities.entities import GameInfo, Referee, Player, Coach, Team


class Basket(object):
    
    game_info = GameInfo()
    home_team = Team()
    away_team = Team()
    
    def __init__(self):
        pass
        
    def interpret(self, model):
        
        '''Game Info'''
        
        self.game_info.city = model.info.city
        self.game_info.arena = model.info.arena
        self.game_info.date = model.info.date
        self.game_info.time = model.info.time
        self.game_info.attendance = model.info.att
        
        '''Referees'''
        
        referees = []
        
        referees.append(Referee(model.info.Referees.first.firstName, model.info.Referees.first.lastName, model.info.Referees.first.nat, "CrewChief"))
        referees.append(Referee(model.info.Referees.second.firstName, model.info.Referees.second.lastName, model.info.Referees.second.nat, "Referee"))
        referees.append(Referee(model.info.Referees.third.firstName, model.info.Referees.third.lastName, model.info.Referees.third.nat, "Umpire"))
        
        self.game_info.referees = referees     
        
        '''Teams - Home and Away'''
        
        homePlayers = []
        awayPlayers = []                       
        
        for n in model.info.homeTeam.players:
            p = Player(n.firstName, n.lastName, n.nat, n.number, n.position)                     
            homePlayers.append(p)
                         
        self.home_team.name = model.info.homeTeam.name
        self.home_team.coach = Coach(model.info.homeTeam.coach.firstName, model.info.homeTeam.coach.lastName, model.info.homeTeam.coach.nat)
        self.home_team.players = homePlayers
        
        for n in model.info.awayTeam.players:
            p = Player(n.firstName, n.lastName, n.nat, n.number, n.position)                     
            awayPlayers.append(p)
                    
        self.away_team.name = model.info.awayTeam.name
        self.away_team.coach = Coach(model.info.awayTeam.coach.firstName, model.info.awayTeam.coach.lastName, model.info.awayTeam.coach.nat)
        self.away_team.players = awayPlayers
        
        '''Events'''       
        
        for p in model.periods:
            for e in p.events:
                if e.__class__.__name__ == "FreeThrow":
                    self.freeThrow(e.team, e.player, e.made)                   
                elif e.__class__.__name__ == "TwoPoints":
                    self.twoPoints(e.team, e.player, e.made)
                    if e.ast != None:
                        self.assist(e.team, e.ast.player)                                      
                elif e.__class__.__name__ == "ThreePoints":
                    self.threePoints(e.team, e.player, e.made)
                    if e.ast != None:
                        self.assist(e.team, e.ast.player)             
                elif e.__class__.__name__ == "PersonalFoul":
                    self.personalFoul(e.foulTeam, e.playerFoul, e.playerFouled)                   
                elif e.__class__.__name__ == "Turnover":
                    self.event(e.team, e.player, e.__class__.__name__)                   
                elif e.__class__.__name__ == "Steal":
                    self.event(e.team, e.player, e.__class__.__name__)                                  
                elif e.__class__.__name__ == "OffensiveRebound":
                    self.event(e.team, e.player, e.__class__.__name__)                   
                elif e.__class__.__name__ == "DefensiveRebound":
                    self.event(e.team, e.player, e.__class__.__name__)                                  
                elif e.__class__.__name__ == "TehnicalFoulPlayer":
                    self.event(e.team, e.player, e.__class__.__name__)                   
                elif e.__class__.__name__ == "UnsportsmanlikeFoul":
                    self.event(e.team, e.player, e.__class__.__name__)
                elif e.__class__.__name__ == "Block":
                    self.block(e.team, e.player, e.playerBlocked)                  
                elif e.__class__.__name__ == "TehnicalFoulTeam":
                    self.tehnicalFoul(e.team)                   
                elif e.__class__.__name__ == "TehnicalFoulCoach":
                    self.tehnicalFoul(e.team)
                            
    def __str__(self):
        pass
    
    def freeThrow(self, team, player, made):
        if team == "Home":
            players = self.home_team.players
        else:
            players = self.away_team.players
        for p in players:
            if p.number == player:
                p.free_throws_attempted += 1
                if made:
                    p.free_throws_made += 1  
    
    def twoPoints(self, team, player, made):
        if team == "Home":
            players = self.home_team.players
        else:
            players = self.away_team.players
        for p in players:
            if p.number == player:
                p.two_points_attempted += 1
                if made:
                    p.two_points_made += 1
    
    def threePoints(self, team, player, made):
        if team == "Home":
            players = self.home_team.players
        else:
            players = self.away_team.players
        for p in players:
            if p.number == player:
                p.three_points_attempted += 1
                if made:
                    p.three_points_made += 1
    
    def assist(self, team, player):
        if team == "Home":
            players = self.home_team.players
        else:
            players = self.away_team.players
        for p in players:
            if p.number == player:
                p.assists += 1
    
    def personalFoul(self, team, playerFoul, playerFouled):       
        if team == "Home":
            for p in self.home_team.players:
                if p.number == playerFoul:
                    p.fouls_commited += 1
            for p in self.away_team.players:
                if p.number == playerFouled:
                    p.fouls_received += 1
        else:
            for p in self.away_team.players:
                if p.number == playerFoul:
                    p.fouls_commited += 1
            for p in self.home_team.players:
                if p.number == playerFouled:
                    p.fouls_received += 1       
    
    def event(self, team, player, event):
        if team == "Home":
            players = self.home_team.players
        else:
            players = self.away_team.players
        
        for p in players:
            if p.number == player:
                if event == "Turnover":
                    p.turnovers += 1
                elif event == "Steal":
                    p.steals += 1
                elif event == "OffensiveRebound":
                    p.rebounds_offensive += 1
                elif event == "DefensiveRebound":
                    p.rebounds_deffensive += 1
                elif event == "TehnicalFoulPlayer":
                    p.fouls_commited += 1
                elif event == "UnsportsmanlikeFoul":
                    p.fouls_commited += 1   
    
    def block(self, team, player, playerBlocked):
        if team == "Home":
            for p in self.home_team.players:
                if p.number == player:
                    p.blocks_in_favor += 1
            for p in self.away_team.players:
                if p.number == playerBlocked:
                    p.blocks_against += 1
        else:
            for p in self.away_team.players:
                if p.number == player:
                    p.blocks_in_favor += 1
            for p in self.home_team.players:
                if p.number == playerBlocked:
                    p.blocks_against += 1           
    
    def tehnicalFoul(self, team):
        if team == "Home":
            self.home_team.tehnicals += 1
        else:
            self.away_team.tehnicals += 1   

if __name__ == '__main__':

    basket_metamodel = metamodel_from_file('grammar.tx', debug=False)
    metamodel_export(basket_metamodel, 'basketStats_meta.dot')
    graph = pydot.graph_from_dot_file('basketStats_meta.dot')
    graph.write_png('basketStats_meta.png')

    basket_model = basket_metamodel.model_from_file('game.tx')
    model_export(basket_model, 'basketStats_model.dot')
    graph = pydot.graph_from_dot_file('basketStats_model.dot')
    graph.write_png('basketStats_model.png')

    basket = Basket()
    basket.interpret(basket_model)

    env = Environment(trim_blocks=True, lstrip_blocks=True, loader=PackageLoader("basket", "templates"))
  
    template = env.get_template("statsTemplate.html")
    t = template.render(game=basket)
    print(t)
    with open("output/stats.html", "w") as f:
        f.write(t)
